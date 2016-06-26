import java.io.IOException;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ImagePacket 
{
    public final String dataString;
    public final String uniqueFileName;

    @JsonCreator
    public ImagePacket(@JsonProperty("dataString") String dataString, 
			   		   @JsonProperty("uniqueFileName") String uniqueFileName)
    {
        this.dataString = dataString;
        this.uniqueFileName = uniqueFileName;
    }

    public String toNetworkString()
    {
        ObjectMapper mapper = new ObjectMapper();
        String str = "";
        try
        {
            str = mapper.writeValueAsString(this);
        } catch (IOException e)
        {
            e.printStackTrace();
        }
        return str;
    }
    
    public static ImagePacket toModel(String json)
    {
    	ObjectMapper mapper = new ObjectMapper();
        try { return mapper.readValue(json, ImagePacket.class); } catch (Exception e) {}
        return null;
    }
}
