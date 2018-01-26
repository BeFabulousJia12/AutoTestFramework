from http.server import HTTPServer
from my_handler import MyHandler


def main():
	try:
		server = HTTPServer(('', 8090), MyHandler)
		print('Welcome to MockDataServer')
		server.serve_forever()
	except KeyboardInterrupt:
		server.socket.close()


if __name__ == '__main__':
	main()
