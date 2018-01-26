from configparser import ConfigParser
import json


class Properties:

	def __init__(self):
		cf = ConfigParser()
		cf.read("mockDataServer.conf")

		self.timeSeriesResponseAmount = cf.getint("responseAmount", "timeSeriesData")
		self.qualityResponseAmount = cf.getint("responseAmount", "qualityData")
		self.goodQualityTimesPerHour = cf.getint("responseAmount", "goodQualityTimesPerHour")
		self.badQualityTimesPerHour = cf.getint("responseAmount", "badQualityTimesPerHour")
		self.reworkQualityTimesPerHour = cf.getint("responseAmount", "reworkQualityTimesPerHour")

		self.qualityResponsePath = cf.get("filePath", "qualityOneTimeSeriesResp")
		self.seriesResponsePath = cf.get("filePath", "oneTimeSeriesResp")
		self.mockListPath = cf.get("filePath", "mockList")
		self.identifierResponsePath = cf.get("filePath", "oneTimeIdentifier")

		self.timeSeriesRequestEndpoint = cf.get("requestEndpoint", "timeSeriesEndpoint")
		self.assetsRequestEndpoint = cf.get("requestEndpoint", "assetsEndpoint")
		self.assetHierarchyEndpoint = cf.get("requestEndpoint", "assetHierarchy")

		self.requestPeriod = cf.get("requestPeriod(s)", "requestPeriod")

		self.goodQualityInitialAmount = cf.get("qualityInitialAmount", "goodQualityAmount")
		self.badQualityInitialAmount = cf.get("qualityInitialAmount", "badQualityAmount")
		self.reworkQualityInitialAmount = cf.get("qualityInitialAmount", "reworkQualityAmount")

		self.variableQuality = cf.get("qualityAssetName", "variableQuality")
		self.aspectQuality = cf.get("qualityAssetName", "aspectQuality")

		with open(self.mockListPath, "r+", encoding='UTF-8') as mockListFile:
			self.mockList = json.load(mockListFile)
		with open(self.identifierResponsePath, "r+", encoding='UTF-8') as oneTimeIdentifierFile:
			self.oneTimeIdentifierJson = json.load(oneTimeIdentifierFile)
