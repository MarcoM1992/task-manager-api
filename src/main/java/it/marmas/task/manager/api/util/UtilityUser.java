package it.marmas.task.manager.api.util;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.marmas.task.manager.api.exceptions.AuthorizationException;

public class UtilityUser {
	private static Logger logger= LoggerFactory.getLogger(UtilityUser.class);

 
	public static void checkAuthorization(String currentUser, String nameRetrieved, List<String> currentAuthority) {
		String errorMsg;
		boolean isSameUser= currentUser.equalsIgnoreCase(nameRetrieved);
		 logger.info("is the same user : "+isSameUser);
		 //check if it is admin
		 boolean isAdmin= currentAuthority.stream().anyMatch(x -> x.equalsIgnoreCase("ADMIN")||x.equalsIgnoreCase("ROLE_ADMIN"));
		 logger.info("isAdmin : "+ isAdmin);

		 if(!isSameUser&&!isAdmin) {
			 errorMsg="User "+currentUser+ " is not allowed to execute this operation contact the amministration";
			 logger.warn(errorMsg);
			 throw new AuthorizationException(errorMsg);
		 }
	}
	public static boolean idIsValid(String id) {
		boolean idNotNull=id!=null;
		boolean idIsDigit=Arrays.stream(id.split("")).allMatch(x->Character.isDigit(x.charAt(0)));
		logger.info("id not null "+ idNotNull);
		logger.info("id is digit "+idIsDigit);
 	 return idNotNull && idIsDigit;
	}
	public static LocalDateTime convertTimeWithUserSetting(LocalDateTime createdAtUtc,String zoneId) {
		
		return createdAtUtc.atOffset(ZoneOffset.UTC).atZoneSameInstant(ZoneId.of(zoneId)).toLocalDateTime();
		
	}

}
