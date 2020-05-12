package spotifyPackage;

public class Request implements java.io.Serializable{

    private String header;
    private Object data;

    public Request(String h, Object d){

        header = h;
        data = d;
    }

    public String getHeader() {return header;}
    public Object getData() {return data;}

    public String toString(){
        if(data instanceof String)
            return header + " " + data;
        else
            return header;
    }
}

/* useful headers:
* respectful data:
*
* publisherHello: sent from a publisher to all brokers to notify them he's started
* lastLetter,ip,port
* Should be sent without whitespaces
*
* songPull: sent from a consumer to a broker or from a broker to a publisher to pull a song
* artistName,song
*
* musicData 0/1: used when the data part is a music file
*
* musicDataAck: used as a confirmation when a consumer receives a musicData
* ~~
*
* newConnection: sent from a broker to a consumer to propose a broker which can handle the requested song
* newIP,newPort
*
* newConnectionAck: sent to confirm the establishment of a connection
*
* artistUnavailable: used when an artist is unavailable
* artistName
*
* error: sent when an error occurred
* error message
*
 */