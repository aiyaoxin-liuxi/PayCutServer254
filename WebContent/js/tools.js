function getCurrentContextPath()
{
	var urlHref=location.href;
	var urlArray=urlHref.split("/");
	var currentContextPath=urlArray[0]+"//"+urlArray[2]+"/"+urlArray[3]+"/";
	return currentContextPath;
}