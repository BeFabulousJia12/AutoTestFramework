from properties import Properties
import time
import datetime
import re
import json


class Util:

	def __init__(self):
		self.properties = Properties()

	def getIdNameMap(self):
		mapDic = {}
		for mockJson in self.properties.mockList:
			if mockJson["request"]["uri"] == self.properties.assetHierarchyEndpoint:
				identifierJsonRes = mockJson["response"]["json"]["value"]
				for identifierInfo in identifierJsonRes:
					mapDic[identifierInfo["assetId"]] = identifierInfo["name"]
		return mapDic

	def getIdIdentifierMap(self):
		mapDic = {}
		for mockJson in self.properties.mockList:
			if mockJson["request"]["uri"] == self.properties.assetHierarchyEndpoint:
				identifierJsonRes = mockJson["response"]["json"]["value"]
				for identifierInfo in identifierJsonRes:
					mapDic[identifierInfo["assetId"]] = identifierInfo["identifier"]
		return mapDic

	@staticmethod
	def timeStampToUtcTime(timestamp):
		t = datetime.datetime.fromtimestamp(timestamp)
		utcTime = datetime.datetime.strftime(t, '%Y-%m-%dT%H:%M:%S.%fZ')
		utcTime = re.match(r'.+?\....', utcTime, re.I | re.S).group() + "Z"
		return utcTime

	@staticmethod
	def transToTimestamp(inputtime):
		timeArray = re.split(r'\.|Z', inputtime)
		structedTime = time.strptime(timeArray[0], "%Y-%m-%dT%H:%M:%S")
		timestamp = time.mktime(structedTime)
		print(timestamp)
		return timestamp

	@staticmethod
	def getTimeQuery(query):
		queryJsonStr = re.match(r'filter=({.+?}}})', query, re.I | re.S)
		timeQueryStr = queryJsonStr.group(1)
		queryJson = json.loads(timeQueryStr)
		return queryJson