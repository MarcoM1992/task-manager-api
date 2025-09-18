package it.marmas.task.manager.api.util;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.Date;

public class Utility {
	//private static Logger logger = LoggerFactory.getLogger(Utility.class);
	private static final String DEFAUL_ZONE_ID="UTC";
	//private static final String DEFAUL_ZONE_ID="America/Vancouver";


	public static boolean notNull(Object obj) {
		return obj!=null;
	}
	
 	    public static Date convertToDate(LocalDateTime localDateTime) {
	        if (localDateTime == null) {
	            return null;
	        }
	        return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
	    
	}
 	    
 	   public static LocalDateTime convertToLocalDateTime(Date date) {
 	        if (date == null) {
 	            return null;
 	        }
 	        return date.toInstant()
 	                   .atZone(ZoneId.systemDefault())
 	                   .toLocalDateTime();
 	    }
 	   public static String formatDate(Date date) {
 		   LocalDateTime ldt= date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
 		   DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd/HH:mm:ss");
 		   return formatter.format(ldt);
 	   }
 	   public static String formatLdl(TemporalAccessor ldt) {
  		  ZoneId zone=ZoneId.of(DEFAUL_ZONE_ID);
 		   DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd/HH:mm:ss").withZone(zone);
 		   
 		   return formatter.format(ldt);
 	   }
 	   public static String formatLdl(TemporalAccessor ldt,ZoneId zone) {
   		  
 		   DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd/HH:mm:ss").withZone(zone);
 		   
 		   return formatter.format(ldt);
 	   }
 	   public static LocalDateTime odtToLdt(OffsetDateTime odt) {
 		  return odt
          .withOffsetSameInstant(ZoneOffset.UTC)
          .toLocalDateTime();
 	   }
 	  
 	   
 	   public static OffsetDateTime ldtToOdt(LocalDateTime utcDateTime,ZoneId userZone ) {
 		    if (utcDateTime == null) {
 	            return null;
 	        }
 		    if(userZone==null) {
 		    	userZone=ZoneId.of(DEFAUL_ZONE_ID);
 		    }
 	        // Interpreta LocalDateTime come UTC
 	        OffsetDateTime utcOffset = utcDateTime.atOffset(ZoneOffset.UTC);

 	        // Converte nello stesso istante ma nel fuso orario dell’utente
 	        return utcOffset.atZoneSameInstant(userZone).toOffsetDateTime();
 	    }
 	  

 	    public static Instant localDateTimeToInstant(LocalDateTime localDateTime, String zoneId) {
 	        return localDateTime.atZone(ZoneId.of(zoneId)).toInstant();
 	    }
 		  
 	   public static LocalDateTime instantToLocalDateTime(Instant instant, ZoneId zoneId) {
	    	ZoneId zone =zoneId==null?ZoneId.of(DEFAUL_ZONE_ID):zoneId;
	    			
	        return instant.atZone(zone).toLocalDateTime();
	    }

	   public static String seeTokenEnding(String accessToken) {
		   return accessToken.substring(accessToken.length()-20);
		// TODO Auto-generated method stub
		
	   }
 	   
 	  
 	  
 	   
 	   
 	   
 	  
}
