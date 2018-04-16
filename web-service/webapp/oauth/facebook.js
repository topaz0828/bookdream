// This is called with the results from from FB.getLoginStatus().
function statusChangeCallback(response) {
	// The response object is returned with a status field that lets the
	// app know the current login status of the person.
	// Full docs on the response object can be found in the documentation
	// for FB.getLoginStatus().
	if (response.status === 'connected') {
		// Logged into your app and Facebook.
		document.location = '/';
	} else {
		// The person is not logged into your app or we are unable to tell.
		
	}
}

// This function is called when someone finishes with the Login
// Button.  See the onlogin handler attached to it in the sample
// code below.
function checkLoginState() {
	FB.getLoginStatus(function(response) {
		statusChangeCallback(response);
	});
}

window.fbAsyncInit = function() {
    FB.init({
        appId            : '242352022831116',
        autoLogAppEvents : true,
        xfbml            : true,
        version          : 'v2.12'
    });
};

(function(d, s, id){
     var js, fjs = d.getElementsByTagName(s)[0];
     if (d.getElementById(id)) {return;}
     js = d.createElement(s); js.id = id;
     js.src = "https://connect.facebook.net/en_US/sdk.js";
     fjs.parentNode.insertBefore(js, fjs);
   }(document, 'script', 'facebook-jssdk'));

// Here we run a very simple test of the Graph API after login is
// successful.  See statusChangeCallback() for when this call is made.
function successSigninFb(authResponse) {
	console.log('Welcome!  Fetching your information.... ');
	FB.api('/me?fields=id,name,picture,email', function(response) {
		console.log('Successful login for: ' + response.name);
		authResponse.email = response.email;
		authResponse.name = response.name;
		authResponse.picture = response.picture.data.url;
		console.log(authResponse);
	});
}

function checkPermission() {
	FB.api('/me/permissions', function(response) {
		console.log('Permission: ');
		console.log(response);
	});
}