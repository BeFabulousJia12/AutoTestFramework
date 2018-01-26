import json
import urllib.request
import data_generate_module as dgm
from properties import Properties
from http.server import BaseHTTPRequestHandler
from util import Util
from pymongo import MongoClient


count_mt1 = 0
count_mt2 = 0
count_good = 0
count_bad = 0
count_rework = 0

class MyHandler(BaseHTTPRequestHandler):
	def do_GET(self):
		queryJson = None
		parsed = urllib.parse.urlparse(self.path)
		serverName = self.server.server_name
		serverPort = self.server.server_port
		urlHead = "http://" + serverName + ":" + str(serverPort)
		properties = Properties()
		global count_mt1
		global count_mt2
		global count_good
		global count_bad
		global count_rework
		if properties.timeSeriesRequestEndpoint in self.path:
			query = urllib.parse.unquote(parsed.query)
			queryJson = Util.getTimeQuery(query)
			mockList = dgm.generateMockMindGateDate(queryJson, properties.mockList, self.path, urlHead)
			content = dgm.moco(self.command, parsed.path, queryJson, mockList)
			if "/api/timeseries/api/search/findTimeseries" in self.path:
				conn = MongoClient('127.0.0.1', 27017)
				db = conn.schaefflerTest
				my_set = db.TimeSeriesData
				if queryJson['assetId'] == "00E30D9EF86A4C49BEC18678750DCFD2":
					count_mt1 +=1
					my_set.insert({"assetId":queryJson['assetId'],"No":count_mt1,"content":str(content,encoding = "utf8")})
				if queryJson['assetId']=="5cd3f25e8e034866a8123b88d4b00dd3":
					count_mt2 +=1
					my_set.insert({"assetId":queryJson['assetId'],"No":count_mt2,"content":str(content,encoding = "utf8")})
				if queryJson['aspectName']=="Good":
					count_good +=1
					my_set.insert({"aspectName":queryJson['aspectName'],"No":count_good,"content":str(content,encoding = "utf8")})
				if queryJson['aspectName']=="Bad":
					count_bad +=1
					my_set.insert({"aspectName":queryJson['aspectName'],"No":count_bad,"content":str(content,encoding = "utf8")})
				if queryJson['aspectName']=="Rework":
					count_rework +=1
					my_set.insert({"aspectName":queryJson['aspectName'],"No":count_rework,"content":str(content,encoding = "utf8")})
		elif properties.assetsRequestEndpoint in self.path and "aspects" not in self.path and "assets/" not in self.path:
			identifierInfo = dgm.generateIdentifierInfo(properties.oneTimeIdentifierJson, self.path, urlHead)
			respStr = json.dumps(identifierInfo)
			content = bytes(respStr, "utf8")
		else:
			content = dgm.moco(self.command, parsed.path, queryJson, properties.mockList)
		try:
			self.send_response(200)
			self.send_header("Content-Length", str(len(content)))
		except BaseException:
			self.send_error(400, "Invalid request url")
		else:
			self.send_header('Content-type', 'application/json')
			self.end_headers()
			self.wfile.write(content)





