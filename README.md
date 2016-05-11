Activator template for Play Framework and the Slick database access library
This template helps building a classic web app or a JSON API

For a more complex example, see the [computer database sample](https://github.com/playframework/play-slick/tree/master/samples/computer-database)


Supported API :

GET		/category

GET		/category/find

POST	/category/insert

POST	/category/update

DELETE	/category/delete

GET		/category/up     

GET		/category/down

GET		/product	  

GET		/product/find

Required Header :

Date : "HTTP-date" format as defined by RFC 7231 Date/Time Formats ex : [Tue, 15 Nov 1994 08:12:31 GMT]

Authorization : MD5(appSecret + date + paramArgs + stringBody)

	- appSecret : located in conf/application.conf

	- date : Date header

	- paramArgs : appended parameters. ex : /category/find?id=1&name=Fashion will be 1Fashion

	- stringBody : body with removed all whitespace. ex : {"id":5,"name":"Fashion Pria","parent":1}