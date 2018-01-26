import json
import random
import re
import copy
from util import Util
from properties import Properties

qualityCount = 1
util = Util()
properties = Properties()
statusCount = 0
goodQualityCount = int(properties.goodQualityInitialAmount)
#goodQualityCount = int(input("Please input the initial amount of Good quality : "))
badQualityCount = int(properties.badQualityInitialAmount)
#badQualityCount = int(input("Please input the initial amount of Bad quality : "))
reworkQualityCount = int(properties.reworkQualityInitialAmount)
#reworkQualityCount = int(input("Please input the initial amount of Rework quality : "))


def generateRandSnapShot(startSeconds, endSeconds, urlSelfPath, queryJson, urlHead):
	variableName = None
	qualityExist = False
	amountInfoDic = {}
	markQuality = int
	oneTimeRespFile = open(properties.seriesResponsePath, "r+", encoding='UTF-8')
	responseCount = properties.timeSeriesResponseAmount
	qualityTimesPerHour = properties.goodQualityTimesPerHour
	global qualityCount

	for assetId, assetName in util.getIdNameMap().items():
		if assetId in urlSelfPath:
			if 'Quality' in assetName and properties.variableQuality not in assetName:
				oneTimeRespFile = open(properties.qualityResponsePath, "r+", encoding='UTF-8')
				responseCount = properties.qualityResponseAmount
				qualityExist = True
				if "Good" in urlSelfPath:
					variableName = "Good_Piece_Num"
					global goodQualityCount
					qualityCount = goodQualityCount
					markQuality = 1
				elif "Bad" in urlSelfPath:
					variableName = "Bad_Piece_Num"
					global badQualityCount
					qualityCount = badQualityCount
					qualityTimesPerHour = properties.badQualityTimesPerHour
					markQuality = 2
				else:
					variableName = "Rework_Piece_Num"
					global reworkQualityCount
					qualityCount = reworkQualityCount
					qualityTimesPerHour = properties.reworkQualityTimesPerHour
					markQuality = 3
			elif properties.variableQuality == assetName:
				oneTimeRespFile = open(properties.qualityResponsePath, "r+", encoding='UTF-8')
				markQuality = 4

	amountInfoDic['qualityExist'] = qualityExist
	amountInfoDic['variableName'] = variableName
	amountInfoDic['markQuality'] = markQuality
	amountInfoDic['responseCount'] = responseCount
	amountInfoDic['qualityTimesPerHour'] = qualityTimesPerHour
	oneTimeSeriesRespJson = json.load(oneTimeRespFile)

	oneTimeSeriesRespJson['snapshots'] = generateSnapListData(startSeconds, endSeconds, oneTimeSeriesRespJson, urlHead,
															  urlSelfPath, amountInfoDic)

	return oneTimeSeriesRespJson


def generateTimeListData(startSeconds, endSeconds, oneTimeSeriesRespJson, urlHead, urlSelfPath, amountInfoDic):
	if endSeconds - startSeconds > 2000:
		hasNext = random.choice([False, True])
	else:
		hasNext = False

	linkJson = oneTimeSeriesRespJson['_links']
	linkJson['self']['href'] = urlHead + urlSelfPath
	responseCount = amountInfoDic['responseCount']
	if hasNext:
		endSeconds = random.randint(startSeconds, endSeconds)
	else:
		del linkJson['next']
	timeset = set()
	while responseCount > 0:
		responseCount -= 1
		try:
			timeset.add(random.uniform(startSeconds, endSeconds))
		except BaseException:
			"Without next page"
		else:
			hasNext = False
	timelist = list(timeset)
	timelist.sort()
	if hasNext:
		nextUrl = re.sub("\d{4}-\d{2}-\d{2}T.+?Z", Util.timeStampToUtcTime(timelist[-2]), urlSelfPath, 1)
		linkJson['next']['href'] = urlHead + nextUrl

	return timelist


def generate3variables(oneSnapShotJson):
	snapShotList = []
	variableList = oneSnapShotJson['variables']
	variableGoodJson = variableList[0]
	variableBadJson = copy.deepcopy(variableGoodJson)
	variableBadJson['name'] = "Bad_Piece_Num"
	variableReworkJson = copy.deepcopy(variableGoodJson)
	variableReworkJson['name'] = "Rework_Piece_Num"
	snapShotList.append(variableGoodJson)
	snapShotList.append(variableBadJson)
	snapShotList.append(variableReworkJson)
	oneSnapShotJson['variables'] = snapShotList
	return oneSnapShotJson


def generateSnapListData(startSeconds, endSeconds, oneTimeSeriesRespJson, urlHead, urlSelfPath, amountInfoDic):
	oneSnapShotList = oneTimeSeriesRespJson['snapshots']
	oneSnapShotJson = copy.deepcopy(oneSnapShotList[0])
	responseTimesPerHour = 3600/int(properties.requestPeriod) * int(properties.qualityResponseAmount)
	markQuality = int
	timelist = generateTimeListData(startSeconds, endSeconds, oneTimeSeriesRespJson, urlHead, urlSelfPath, amountInfoDic)
	timeSeriesLength = len(timelist)
	snapList = []
	count = 0
	if amountInfoDic['markQuality'] is 4:
		oneSnapShotJson = generate3variables(oneSnapShotJson)
	global qualityCount
	global goodQualityCount, badQualityCount, reworkQualityCount
	global statusCount
	for oneVarDataJson in oneSnapShotJson['variables']:
		oneVarDataJson['value'] = str(round(random.uniform(0, 100), 1))
		oneVarDataJson['qualityCode'] = '00000000'

		# randomCount = int((
		# 	1 if (random.randint(0, responseTimesPerHour) // int(amountInfoDic['qualityTimesPerHour']) == 0) else 0))
		randomCount = random.randint(0, 1)
		if amountInfoDic['qualityExist'] is not True:
			if oneVarDataJson['name'] == "NCProgramStatus":
				if statusCount <= 240:
					oneVarDataJson['value'] = str(3) + ".0"
				elif statusCount <= 300:
					oneVarDataJson['value'] = str(4) + ".0"
				else:
					oneVarDataJson['value'] = str(4) + ".0"
					statusCount = 0
				statusCount += 1
			elif oneVarDataJson['name'] == "Feedoverride":
				oneVarDataJson['value'] = str(random.randint(3, 6) * 11) + ".0"
		else:
			oneVarDataJson['name'] = amountInfoDic['variableName']
			if oneVarDataJson['name'] == "Good_Piece_Num":
				goodQualityCount += randomCount
				oneVarDataJson['value'] = goodQualityCount
			if oneVarDataJson['name'] == "Bad_Piece_Num":
				badQualityCount += (random.randint(1, 100) // 95)
				oneVarDataJson['value'] = badQualityCount
			if oneVarDataJson['name'] == "Rework_Piece_Num":
				reworkQualityCount += randomCount
				oneVarDataJson['value'] = reworkQualityCount
			qualityCount += randomCount

	while count < timeSeriesLength:
		tempOneSnapShotJson = copy.deepcopy(oneSnapShotJson)
		utcTime = Util.timeStampToUtcTime(timelist[count])
		tempOneSnapShotJson['time'] = utcTime
		snapList.append(tempOneSnapShotJson)
		count += 1
	if markQuality == 1:
		goodQualityCount = qualityCount
	elif markQuality == 2:
		badQualityCount = qualityCount
	elif markQuality == 3:
		reworkQualityCount = qualityCount

	return snapList


def generateIdentifierInfo(IdentifierJson, urlSelfPath, urlHead):
	identifierList = []
	embeddedJson = {}
	linksJson = {}
	identifierResponseJson = {}
	pageJson = {}
	hrefJson = {}
	for assetId, assetName in util.getIdIdentifierMap().items():
		IdentifierJson["assetId"] = assetId
		IdentifierJson["identifier"] = assetName
		IdentifierJson["name"] = util.getIdNameMap()[assetId]
		tempOneSnapShotJson = copy.deepcopy(IdentifierJson)
		identifierList.append(tempOneSnapShotJson)

	embeddedJson["assetResources"] = identifierList
	identifierResponseJson["_embedded"] = embeddedJson
	requestUri = urlHead + urlSelfPath + "?" + "page=" + str(random.randint(1, 5)) + "&size=10&sort=name,asc"
	hrefJson["href"] = requestUri
	linksJson["first"] = hrefJson
	linksJson["self"] = hrefJson
	linksJson["last"] = hrefJson
	identifierResponseJson["_links"] = linksJson
	pageJson["size"] = 10
	pageJson["totalElements"] = random.randint(30, 40)
	pageJson["totalPages"] = 3
	pageJson["number"] = 0
	identifierResponseJson["page"] = pageJson
	return identifierResponseJson


def generateMockMindGateDate(queryJson, mockList, urlSelfPath, urlHead):
	try:
		startTime = queryJson['time']['ge']['value']
		endTime = queryJson['time']['le']['value']
		startSeconds = Util.transToTimestamp(startTime)
		endSeconds = Util.transToTimestamp(endTime)
		assetID = queryJson['assetId']
		oneTimeSeriesRespJson = generateRandSnapShot(startSeconds, endSeconds, urlSelfPath, queryJson, urlHead)
		aspectName = queryJson['aspectName']
		for oneQuest in mockList:
			queryStr = oneQuest['request']['uri']
			if properties.timeSeriesRequestEndpoint in queryStr:
				queriesStrInFile = oneQuest['request']['queries']['filter']
				queriesJsonInFile = json.loads(queriesStrInFile)

				if (queriesJsonInFile['assetId'] == assetID and queriesJsonInFile[
					'aspectName'] == aspectName):
					oneQuest['response']['json'] = oneTimeSeriesRespJson
					queriesJsonInFile['time']['ge']['value'] = startTime
					queriesJsonInFile['time']['le']['value'] = endTime
					oneQuest['request']['queries']['filter'] = json.dumps(
						queriesJsonInFile).replace(" ", "")
			else:
				continue
	except IOError:
		content = 'File Not Found'

	return mockList


def moco(command, path, queryJson, mockList):
	for singleResp in mockList:
		singleRequest = singleResp['request']
		singleHttpMethod = singleRequest['method']
		singleUri = singleRequest['uri']
		singleQuery = (singleRequest['queries']['filter'] if singleRequest['queries'] is not None else str(
			None))
		singleQueryStr = str(queryJson).replace("'", "\"").replace(" ", "")
		if singleHttpMethod.lower() == command.lower() and path == singleUri and singleQuery == singleQueryStr:
			respStr = json.dumps(singleResp['response']['json'])
			return bytes(respStr, "utf8")
