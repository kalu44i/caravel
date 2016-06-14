var page = require('webpage').create();
page.viewportSize = {width: 1280, height: 1024};
//page.settings.userAgent = 'Mozilla/6.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/37.0.2062.120 Safari/537.36';
page.settings.userAgent = 'Mozilla/5.0 (Windows NT 6.1; WOW64; Trident/7.0; AS; rv:11.0) like Gecko';

page.settings.userName = 'plugin@metricinsightstest.onmicrosoft.com';
page.settings.password = 'Ins1ght!';

page.customHeaders = {
  "Accept-Language": "en"
};

page.open('https://app.powerbi.com', function() {
	setTimeout(function() { 
		page.render('pbi/db-1.png');
		//phantom.exit();
	}, 5*1000);
	
	setTimeout(function() { 
		page.render('pbi/db-2.png');
		phantom.exit();
	}, 10*1000);
});