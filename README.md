This is my very first trial on developing REST Application using Scala with no prior knowledge of Scala itself. 
Consider the code buggy. Will be fixed and optimized in near future.

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