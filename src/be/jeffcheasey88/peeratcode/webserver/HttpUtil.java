package be.jeffcheasey88.peeratcode.webserver;

import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.simple.parser.JSONParser;

public class HttpUtil {
	
	private HttpUtil(){}
	
	public static void responseHeaders(HttpWriter writer, int code, String... headers) throws Exception{
		writer.write("HTTP/1.1 "+code+" "+codeMessage(code)+"\n");
		for(String header : headers) writer.write(header+"\n");
		writer.write("\n");
		writer.flush();
	}
	
	public static void skipHeaders(HttpReader reader) throws Exception{
		String line;
		while(((line = reader.readLine()) != null) && (line.length() > 0));
	}
	
	public static List<String> readMultiPartData(HttpReader reader) throws Exception{
		List<String> list = new ArrayList<>();
		
		reader.readLine();
		
		while(reader.ready()){
			String line;
			while(((line = reader.readLine()) != null) && (line.length() > 0)){
				
			}
			String buffer = "";
			while(((line = reader.readLine()) != null) && (!line.startsWith("------WebKitFormBoundary"))){
				buffer+=line;
			}
			list.add(buffer);
		}
		
		return list;
	}
	
	public static void switchToWebSocket(HttpReader reader, HttpWriter writer) throws Exception{
		String key = readWebSocketKey(reader);
		if(key == null) throw new IllegalArgumentException();
		
		writer.write("HTTP/1.1 101 Switching Protocols\n");
		writer.write("Connection: Upgrade\n");
		writer.write("Upgrade: websocket\n");
		writer.write("Sec-WebSocket-Accept: "+
				printBase64Binary(
						MessageDigest.getInstance("SHA-1").
							digest((key+"258EAFA5-E914-47DA-95CA-C5AB0DC85B11").getBytes("UTF-8")))+"\n");
		writer.write("\n");
		writer.flush();
	}
	
	private static Pattern WEBSOCKET_KEY = Pattern.compile("Sec-WebSocket-Key: (.*)");
	
	public static String readWebSocketKey(HttpReader reader) throws Exception {
		String line;
		String key = null;
		while(((line = reader.readLine()) != null) && (line.length() > 0)){
			if(key != null){
				continue;
			}
			Matcher matcher = WEBSOCKET_KEY.matcher(line);
			if(matcher.matches()) key = matcher.group(1);
		}
		return key;
	}
	
	private static Pattern AUTORIZATION = Pattern.compile("Autorization: Bearer (.*)");
	
	public static String readAutorization(HttpReader reader) throws Exception {
		String line;
		String key = null;
		while(((line = reader.readLine()) != null) && (line.length() > 0)){
			Matcher matcher = AUTORIZATION.matcher(line);
			if(matcher.matches()){
				key = matcher.group(1);
				break;
			}
		}
		return key;
	}
	
	public static Object readJson(HttpReader reader) throws Exception{
		String line = "";
		while(reader.ready()){
			char[] c = new char[1];
			reader.read(c);
			line+=c[0];
			if(c[0] == '}'){
				Object parse;
				try {
					parse = new JSONParser().parse(line);
					if(parse != null) return parse;
				}catch(Exception e){}
			}
		}
		return null;
	}
	
	//I found this code on StackOverFlow !!!!! (and the write too)
	public static String readWebSocket(HttpReader reader) throws Exception{
		int buffLenth = 1024;
		int len = 0;            
        byte[] b = new byte[buffLenth];
        //rawIn is a Socket.getInputStream();
        while(true){
            len = reader.read(b);
            if(len!=-1){
                byte rLength = 0;
                int rMaskIndex = 2;
                int rDataStart = 0;
                //b[0] is always text in my case so no need to check;
                byte data = b[1];
                byte op = (byte) 127;
                rLength = (byte) (data & op);

                if(rLength==(byte)126) rMaskIndex=4;
                if(rLength==(byte)127) rMaskIndex=10;

                byte[] masks = new byte[4];

                int j=0;
                int i=0;
                for(i=rMaskIndex;i<(rMaskIndex+4);i++){
                    masks[j] = b[i];
                    j++;
                }

                rDataStart = rMaskIndex + 4;

                int messLen = len - rDataStart;

                byte[] message = new byte[messLen];

                for(i=rDataStart, j=0; i<len; i++, j++){
                    message[j] = (byte) (b[i] ^ masks[j % 4]);
                }
                
                return new String(message);

            }else break;
        }
        return null;
	}
	
	public static void sendWebSocket(HttpWriter writer, String message) throws Exception{
		byte[] rawData = message.getBytes();

        int frameCount  = 0;
        byte[] frame = new byte[10];

        frame[0] = (byte) 129;

        if(rawData.length <= 125){
            frame[1] = (byte) rawData.length;
            frameCount = 2;
        }else if(rawData.length >= 126 && rawData.length <= 65535){
            frame[1] = (byte) 126;
            int len = rawData.length;
            frame[2] = (byte)((len >> 8 ) & (byte)255);
            frame[3] = (byte)(len & (byte)255); 
            frameCount = 4;
        }else{
            frame[1] = (byte) 127;
            int len = rawData.length;
            frame[2] = (byte)((len >> 56 ) & (byte)255);
            frame[3] = (byte)((len >> 48 ) & (byte)255);
            frame[4] = (byte)((len >> 40 ) & (byte)255);
            frame[5] = (byte)((len >> 32 ) & (byte)255);
            frame[6] = (byte)((len >> 24 ) & (byte)255);
            frame[7] = (byte)((len >> 16 ) & (byte)255);
            frame[8] = (byte)((len >> 8 ) & (byte)255);
            frame[9] = (byte)(len & (byte)255);
            frameCount = 10;
        }

        int bLength = frameCount + rawData.length;

        byte[] reply = new byte[bLength];

        int bLim = 0;
        for(int i=0; i<frameCount;i++){
            reply[bLim] = frame[i];
            bLim++;
        }
        for(int i=0; i<rawData.length;i++){
            reply[bLim] = rawData[i];
            bLim++;
        }
        
        writer.write(reply);
        writer.flush();
	}

	private static String codeMessage(int paramInt) {
	    switch (paramInt) {
	      case 200:
	        return " OK";
	      case 100:
	        return " Continue";
	      case 201:
	        return " Created";
	      case 202:
	        return " Accepted";
	      case 203:
	        return " Non-Authoritative Information";
	      case 204:
	        return " No Content";
	      case 205:
	        return " Reset Content";
	      case 206:
	        return " Partial Content";
	      case 300:
	        return " Multiple Choices";
	      case 301:
	        return " Moved Permanently";
	      case 302:
	        return " Temporary Redirect";
	      case 303:
	        return " See Other";
	      case 304:
	        return " Not Modified";
	      case 305:
	        return " Use Proxy";
	      case 400:
	        return " Bad Request";
	      case 401:
	        return " Unauthorized";
	      case 402:
	        return " Payment Required";
	      case 403:
	        return " Forbidden";
	      case 404:
	        return " Not Found";
	      case 405:
	        return " Method Not Allowed";
	      case 406:
	        return " Not Acceptable";
	      case 407:
	        return " Proxy Authentication Required";
	      case 408:
	        return " Request Time-Out";
	      case 409:
	        return " Conflict";
	      case 410:
	        return " Gone";
	      case 411:
	        return " Length Required";
	      case 412:
	        return " Precondition Failed";
	      case 413:
	        return " Request Entity Too Large";
	      case 414:
	        return " Request-URI Too Large";
	      case 415:
	        return " Unsupported Media Type";
	      case 500:
	        return " Internal Server Error";
	      case 501:
	        return " Not Implemented";
	      case 502:
	        return " Bad Gateway";
	      case 503:
	        return " Service Unavailable";
	      case 504:
	        return " Gateway Timeout";
	      case 505:
	        return " HTTP Version Not Supported";
	    } 
	    return " ";
	  }

	
	//From javax.xml.bind.DatatypeConverter
	private static String printBase64Binary(byte[] array){
		char[] arrayOfChar = new char[(array.length + 2) / 3 * 4];
	    int i = _printBase64Binary(array, 0, array.length, arrayOfChar, 0);
	    assert i == arrayOfChar.length;
	    return new String(arrayOfChar);
	}
	
	private static int _printBase64Binary(byte[] paramArrayOfbyte, int paramInt1, int paramInt2, char[] paramArrayOfchar, int paramInt3) {
	    int i = paramInt2;
	    int j;
	    for (j = paramInt1; i >= 3; j += 3) {
	      paramArrayOfchar[paramInt3++] = encode(paramArrayOfbyte[j] >> 2);
	      paramArrayOfchar[paramInt3++] = encode((paramArrayOfbyte[j] & 0x3) << 4 | paramArrayOfbyte[j + 1] >> 4 & 0xF);
	      paramArrayOfchar[paramInt3++] = encode((paramArrayOfbyte[j + 1] & 0xF) << 2 | paramArrayOfbyte[j + 2] >> 6 & 0x3);
	      paramArrayOfchar[paramInt3++] = encode(paramArrayOfbyte[j + 2] & 0x3F);
	      i -= 3;
	    } 
	    if (i == 1) {
	      paramArrayOfchar[paramInt3++] = encode(paramArrayOfbyte[j] >> 2);
	      paramArrayOfchar[paramInt3++] = encode((paramArrayOfbyte[j] & 0x3) << 4);
	      paramArrayOfchar[paramInt3++] = '=';
	      paramArrayOfchar[paramInt3++] = '=';
	    } 
	    if (i == 2) {
	      paramArrayOfchar[paramInt3++] = encode(paramArrayOfbyte[j] >> 2);
	      paramArrayOfchar[paramInt3++] = encode((paramArrayOfbyte[j] & 0x3) << 4 | paramArrayOfbyte[j + 1] >> 4 & 0xF);
	      paramArrayOfchar[paramInt3++] = encode((paramArrayOfbyte[j + 1] & 0xF) << 2);
	      paramArrayOfchar[paramInt3++] = '=';
	    } 
	    return paramInt3;
	  }
	
	private static char encode(int paramInt) {
	    return encodeMap[paramInt & 0x3F];
	  }
	private static final char[] encodeMap = initEncodeMap();
	
	private static char[] initEncodeMap() {
	    char[] arrayOfChar = new char[64];
	    byte b;
	    for (b = 0; b < 26; b++)
	      arrayOfChar[b] = (char)(65 + b); 
	    for (b = 26; b < 52; b++)
	      arrayOfChar[b] = (char)(97 + b - 26); 
	    for (b = 52; b < 62; b++)
	      arrayOfChar[b] = (char)(48 + b - 52); 
	    arrayOfChar[62] = '+';
	    arrayOfChar[63] = '/';
	    return arrayOfChar;
	  }
}
