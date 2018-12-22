package scw.net.http.enums;

public enum Header {
	Accept("Accept"),
	Accept_Charset("Accept-Charset"),
	Accept_Encoding("Accept-Encoding"),
	Accept_Language("Accept-Language"),
	Accept_Ranges("Accept-Ranges"),
	Access_Control_Allow_Credentials("Access-Control-Allow-Credentials"),
	Access_Control_Allow_Headers("Access-Control-Allow-Headers"),
	Access_Control_Allow_Methods("Access-Control-Allow-Methods"),
	Access_Control_Allow_Origin("Access-Control-Allow-Origin"),
	Access_Control_Expose_Headers("Access-Control-Expose-Headers"),
	Access_Control_Max_Age("Access-Control-Max-Age"),
	Access_Control_Request_Headers("Access-Control-Request-Headers"),
	Access_Control_Request_Method("Access-Control-Request-Method"),
	Age("Age"),
	Allow("Allow"),
	Authorization("Authorization"),
	Cache_Control("Cache-Control"),
	Connection("Connection"),
	Content_Disposition("Content-Disposition"),
	Content_Encoding("Content-Encoding"),
	Content_Language("Content-Language"),
	Content_Length("Content-Length"),
	Content_Location("Content-Location"),
	Content_Range("Content-Range"),
	Content_Security_Policy("Content-Security-Policy"),
	Content_Security_Policy_Report_Only("Content-Security-Policy-Report-Only"),
	Content_Type("Content-Type"),
	Cookie("Cookie"),
	Cookie2("Cookie2"),
	DNT("DNT"),
	Date("Date"),
	ETag("ETag"),
	Expect("Expect"),
	Expect_CT("Expect-CT"),
	Expires("Expires"),
	Forwarded("Forwarded"),
	From("From"),
	Host("Host"),
	If_Match("If-Match"),
	If_Modified_Since("If-Modified-Since"),
	If_None_Match("If-None-Match"),
	If_Range("If-Range"),
	If_Unmodified_Since("If-Unmodified-Since"),
	Keep_Alive("Keep-Alive"),
	Large_Allocation("Large-Allocation"),
	Last_Modified("Last-Modified"),
	Location("Location"),
	Origin("Origin"),
	Pragma("Pragma"),
	Proxy_Authenticate("Proxy-Authenticate"),
	Proxy_Authorization("Proxy-Authorization"),
	Public_Key_Pins("Public-Key-Pins"),
	Public_Key_Pins_Report_Only("Public-Key-Pins-Report-Only"),
	Range("Range"),
	Referer("Referer"),
	Referrer_Policy("Referrer-Policy"),
	Retry_After("Retry-After"),
	Server("Server"),
	Set_Cookie("Set-Cookie"),
	Set_Cookie2("Set-Cookie2"),
	SourceMap("SourceMap"),
	TE("TE"),
	Timing_Allow_Origin("Timing-Allow-Origin"),
	Tk("Tk"),
	Trailer("Trailer"),
	Transfer_Encoding("Transfer-Encoding"),
	Upgrade_Insecure_Requests("Upgrade-Insecure-Requests"),
	User_Agent("User-Agent"),
	Vary("Vary"),
	Via("Via"),
	WWW_Authenticate("WWW-Authenticate"),
	Warning("Warning"),
	X_Content_Type_Options("X-Content-Type-Options"),
	X_Forwarded_For("X-Forwarded-For"),
	X_Forwarded_Host("X-Forwarded-Host"),
	X_Forwarded_Proto("X-Forwarded-Proto"),
	/**
	 * ajax请求头
	 */
	X_Requested_With("X-Requested-With"),
	X_XSS_Protection("X-XSS-Protection"),
	;
	
	private String value;
	Header(String value) {
		this.value = value;
	}
	
	public String getValue(){
		return value;
	}
	
	public static StringBuilder merge(String connectionCharacter, Header ...types){
		StringBuilder sb = new StringBuilder();
		if(types != null){
			for(int i=0; i<types.length; i++){
				if(i != 0){
					sb.append(connectionCharacter);
				}
				sb.append(types[i].getValue());
			}
		}
		return sb;
	}
	
	@Override
	public String toString() {
		return getValue();
	}
}
