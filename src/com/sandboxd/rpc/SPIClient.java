package com.sandboxd.rpc;

import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfig;

/**
 * The primary access point for communicating with the SPI backend. A valid configuration file will need to be passed via setConfig before any communication can take place.
 */
public class SPIClient extends XmlRpcClient {
	
	private static final String VERSION = "1.1";		//Targeted API version
	
	private static final int ERR_USER_DOES_NOT_EXIST = 1;
	private static final int ERR_SESSION_NOT_VALID = 2;
	private static final int ERR_GROUP_DOES_NOT_EXIST = 3;
	private static final int ERR_ACHIEVEMENT_DOES_NOT_EXIST = 4;
	private static final int ERR_USER_NOT_REGISTERED = 5;
	private static final int ERR_STAT_DOES_NOT_EXIST = 6;
	private static final int ERR_GAME_DOES_NOT_EXIST = 7;
	private static final int ERR_API_KEY_NOT_VALID = 8;
	private static final int ERR_IP_NOT_VALID = 9;
	private static final int ERR_INVALID_FUNCTION = 10;
	private static final int ERR_UNKNOWN = 11;
	private static final int ERR_VERSION = 12;
	private static final int ERR_TRANSACTION_EXISTS = 13;
	private static final int ERR_TRANSACTION_DECLINED = 14;
	private static final int ERR_GUESTS_INVALID = 15;
	private static final int ERR_INPUT = 16;
	
	private static final String GETUSER = "spi.getuser";
	private static final String GETGROUP = "spi.getgroup";
	private static final String ACHIEVEMENT = "spi.achievement";
	private static final String STAT = "spi.stat";
	private static final String LOCATION = "spi.location";
	private static final String TRANSACTION = "spi.transaction";
	
	private int gameid = 0;
	private String apiKey = "";
	
	@Override
	public void setConfig (XmlRpcClientConfig config) {
		if (config instanceof SPIConfigImpl) {
			SPIConfigImpl spiConfig = (SPIConfigImpl)config;
			gameid = spiConfig.getGameId();
			apiKey = spiConfig.getAPIKey();
		}
		
		super.setConfig(config);
	}
	
	private final List<Integer> getIntegerList (Object[] objList) {
		List<Integer> list = new ArrayList<Integer>();
		for (int i = 0; i < objList.length; i++) {
			list.add((Integer)objList[i]);
		}
		return list;
	}
	
	private final List<String> getStringList (Object[] objList) {
		List<String> list = new ArrayList<String>();
		for (int i = 0; i < objList.length; i++) {
			list.add((String)objList[i]);
		}
		return list;
	}
	
	
	private final String generateNonce () {
		final char[] VALID_NONCE_CHARS = new char[] { 'a', 'b', 'c' , 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9' };
		String str = "";
		for (int i = 0; i < 10; i++) {
			str += VALID_NONCE_CHARS[(int)(Math.random() * VALID_NONCE_CHARS.length)];
		}
		return str;
	}
	
	private String convToHex(byte[] data) {
        StringBuilder buf = new StringBuilder();
        for (int i = 0; i < data.length; i++) {
            int halfbyte = (data[i] >>> 4) & 0x0F;
            int two_halfs = 0;
            do {
                if ((0 <= halfbyte) && (halfbyte <= 9))
                    buf.append((char) ('0' + halfbyte));
                else
                	buf.append((char) ('a' + (halfbyte - 10)));
                halfbyte = data[i] & 0x0F;
            } while(two_halfs++ < 1);
        }
        return buf.toString();
    }

    private String SHA1 (String text) {
    	try {
		    MessageDigest md = MessageDigest.getInstance("SHA-1");
		    byte[] sha1hash = new byte[40];
		    md.update(text.getBytes("iso-8859-1"), 0, text.length());
		    sha1hash = md.digest();
		    return convToHex(sha1hash);
    	} catch (Exception e) {
    		e.printStackTrace();
    		return null;
    	}
    }
	
	private final Object[] addHeader (Object[] payload) {
		String nonce = generateNonce();
		
		Object[] message = new Object[4 + payload.length];
		message[0] = VERSION;
		message[1] = gameid;
		message[2] = SHA1(apiKey + nonce);
		message[3] = nonce;
		for (int i = 0; i < payload.length; i++) message[i + 4] = payload[i];
		
		return message;
	}
	
	/**
	 * Returns a user object containing detailed information about the user. This method will also perform session id and ip address validation.
	 *
	 * @param uid the unique identification number for the user you want to retrieve
	 * @param sid the session id of the user which is used to verify the user is who they say they are
	 * @return the user object
	 * @see SPIUser
	 */
	@SuppressWarnings("unchecked")
	public SPIUser getUser (int uid, String sid) throws SPIException, XmlRpcException {
		try {
			Object[] result;
			
			if (sid == null) result = (Object[])this.execute(GETUSER, addHeader(new Object[] { uid }));
			else result = (Object[])this.execute(GETUSER, addHeader(new Object[] { uid, sid }));
			
			if (sid != null) return new SPIUser(uid, sid, (String)result[0], (String)result[1], (Integer)result[2], getIntegerList((Object[])result[3]), (Map<String, Integer>)result[4], (Map<String, String>)result[5], getStringList((Object[])result[6]));
			else return new SPIUser(uid, sid, (String)result[0], (String)result[1], (Integer)result[2], getIntegerList((Object[])result[3]), (Map<String, Integer>)result[4], (Map<String, String>)result[5]);
		} catch (XmlRpcException e) {
			switch (e.code) {
			case ERR_USER_DOES_NOT_EXIST:
				throw new UserDoesNotExistException();
			case ERR_SESSION_NOT_VALID:
				throw new SessionNotValidException();
			case ERR_IP_NOT_VALID:
				throw new IPNotValidException();
			case ERR_GAME_DOES_NOT_EXIST:
				throw new GameDoesNotExistException();
			case ERR_API_KEY_NOT_VALID:
				throw new APIKeyNotValidException();
			case ERR_INVALID_FUNCTION:
				throw new InvalidFunctionException();
			case ERR_UNKNOWN:
				throw new SPIException();
			case ERR_VERSION:
				throw new InvalidVersionException();
			case ERR_INPUT:
				throw new InvalidInputException();
			default:
				throw e;
			}
		}
	}

	/**
	 * Returns a user object containing detailed information about the user.
	 *
	 * @param uid the unique identification number for the user you want to retrieve
	 * @return the user object
	 * @see SPIUser
	 */
	public SPIUser getUser (int uid) throws SPIException, XmlRpcException {
		return this.getUser(uid, null);
	}
	
	/**
	 * Returns a group object.
	 *
	 * @param gid the unique identification number for the group you want to retrieve
	 * @return the group object
	 * @see SPIGroup
	 */
	public SPIGroup getGroup (int gid) throws SPIException, XmlRpcException {
		try {
			Object[] result;
			
			result = (Object[])this.execute(GETGROUP, addHeader(new Object[] { gid }));
			
			return new SPIGroup(gid, (String)result[0], getIntegerList((Object[])result[1]));
		} catch (XmlRpcException e) {
			switch (e.code) {
			case ERR_GROUP_DOES_NOT_EXIST:
				throw new GroupDoesNotExistException();
			case ERR_GAME_DOES_NOT_EXIST:
				throw new GameDoesNotExistException();
			case ERR_API_KEY_NOT_VALID:
				throw new APIKeyNotValidException();
			case ERR_INVALID_FUNCTION:
				throw new InvalidFunctionException();
			case ERR_UNKNOWN:
				throw new SPIException();
			case ERR_VERSION:
				throw new InvalidVersionException();
			case ERR_INPUT:
				throw new InvalidInputException();
			default:
				throw e;
			}
		}
	}
	
	/**
	 * Sets an achievement value for the specified user.
	 *
	 * @param uid the unique identification number for the user you want to set the achievement for
	 * @param sid the user's session id used for verification
	 * @param key the achievement identifier
	 * @param value the value to set the achievement to
	 */
	public void setAchievement (int uid, String sid, String key, int value) throws SPIException, XmlRpcException {
		try {
			this.execute(ACHIEVEMENT, addHeader(new Object[] { uid, sid, key, value }));
		} catch (XmlRpcException e) {
			switch (e.code) {
			case ERR_USER_DOES_NOT_EXIST:
				throw new UserDoesNotExistException();
			case ERR_SESSION_NOT_VALID:
				throw new SessionNotValidException();
			case ERR_ACHIEVEMENT_DOES_NOT_EXIST:
				throw new AchievementDoesNotExistException();
			case ERR_USER_NOT_REGISTERED:
				throw new UserNotRegisteredException();
			case ERR_GUESTS_INVALID:
				throw new CannotUseGuestAsUserException();
			case ERR_GAME_DOES_NOT_EXIST:
				throw new GameDoesNotExistException();
			case ERR_API_KEY_NOT_VALID:
				throw new APIKeyNotValidException();
			case ERR_INVALID_FUNCTION:
				throw new InvalidFunctionException();
			case ERR_UNKNOWN:
				throw new SPIException();
			case ERR_VERSION:
				throw new InvalidVersionException();
			case ERR_INPUT:
				throw new InvalidInputException();
			default:
				throw e;
			}
		}
	}

	/**
	 * Sets an statistic value for the specified user.
	 *
	 * @param uid the unique identification number for the user you want to set the statistic for
	 * @param sid the user's session id used for verification
	 * @param key the statistic identifier
	 * @param value the value to set the statistic to
	 */
	public void setStat (int uid, String sid, String key, String value) throws SPIException, XmlRpcException {
		try {
			this.execute(STAT, addHeader(new Object[] { uid, sid, key, value }));
		} catch (XmlRpcException e) {
			switch (e.code) {
			case ERR_USER_DOES_NOT_EXIST:
				throw new UserDoesNotExistException();
			case ERR_SESSION_NOT_VALID:
				throw new SessionNotValidException();
			case ERR_STAT_DOES_NOT_EXIST:
				throw new StatDoesNotExistException();
			case ERR_USER_NOT_REGISTERED:
				throw new UserNotRegisteredException();
			case ERR_GUESTS_INVALID:
				throw new CannotUseGuestAsUserException();
			case ERR_GAME_DOES_NOT_EXIST:
				throw new GameDoesNotExistException();
			case ERR_API_KEY_NOT_VALID:
				throw new APIKeyNotValidException();
			case ERR_INVALID_FUNCTION:
				throw new InvalidFunctionException();
			case ERR_UNKNOWN:
				throw new SPIException();
			case ERR_VERSION:
				throw new InvalidVersionException();
			case ERR_INPUT:
				throw new InvalidInputException();
			default:
				throw e;
			}
		}
	}

	/**
	 * Sets an statistic value for the specified user. Used for ranked (leaderboard) statistics.
	 *
	 * @param uid the unique identification number for the user you want to set the statistic for
	 * @param sid the user's session id used for verification
	 * @param key the statistic identifier
	 * @param value the value to set the statistic to
	 */
	public void setStat (int uid, String sid, String key, double value) throws SPIException, XmlRpcException {
		setStat(uid, sid, key, String.valueOf(value));
	}

	/**
	 * Update a user's location within the game. This can be used by the user's friends to automatically join into the user's game.
	 *
	 * @param uid the unique identification number for the user
	 * @param sid the session id of the user
	 * @param locationdata a machine name used to uniquely determine where the user is within the game
	 * @param location the human-readable name of this location
	 */
	public void setLocation (int uid, String sid, String locationdata, String location) throws SPIException, XmlRpcException {
		try {
			this.execute(LOCATION, addHeader(new Object[] { uid, sid, locationdata, location }));
		} catch (XmlRpcException e) {
			switch (e.code) {
			case ERR_USER_DOES_NOT_EXIST:
				throw new UserDoesNotExistException();
			case ERR_SESSION_NOT_VALID:
				throw new SessionNotValidException();
			case ERR_GUESTS_INVALID:
				throw new CannotUseGuestAsUserException();
			case ERR_GAME_DOES_NOT_EXIST:
				throw new GameDoesNotExistException();
			case ERR_API_KEY_NOT_VALID:
				throw new APIKeyNotValidException();
			case ERR_INVALID_FUNCTION:
				throw new InvalidFunctionException();
			case ERR_UNKNOWN:
				throw new SPIException();
			case ERR_VERSION:
				throw new InvalidVersionException();
			case ERR_INPUT:
				throw new InvalidInputException();
			default:
				throw e;
			}
		}
	}

	/**
	 * Cancels the user's last location. Use this when the user is in an unjoinable location within the game.
	 *
	 * @param uid the unique identification number for the user
	 * @param sid the session id of the user
	 */
	public void setLocation (int uid, String sid) throws SPIException, XmlRpcException {
		setLocation(uid, sid, "", "");
	}
	
	/**
	 * Applies a micro transaction to the user.
	 *
	 * @param uid the unique identification number for the user
	 * @param sid the session id of the user
	 * @param microid a unique identifier for this transaction
	 * @param desc the description to present to the user explaining what they are paying for
	 * @param amount the amount the user has to pay to receive this transaction
	 */
	public void applyTransaction (int uid, String sid, String microid, String desc, int amount) throws SPIException, XmlRpcException {
		try {
			this.execute(TRANSACTION, addHeader(new Object[] { uid, sid, microid, desc, amount }));
		} catch (XmlRpcException e) {
			switch (e.code) {
			case ERR_USER_DOES_NOT_EXIST:
				throw new UserDoesNotExistException();
			case ERR_SESSION_NOT_VALID:
				throw new SessionNotValidException();
			case ERR_TRANSACTION_EXISTS:
				throw new UserAlreadyHasTransactionException();
			case ERR_TRANSACTION_DECLINED:
				throw new UserDeclinedTransactionException();
			case ERR_GUESTS_INVALID:
				throw new CannotUseGuestAsUserException();
			case ERR_GAME_DOES_NOT_EXIST:
				throw new GameDoesNotExistException();
			case ERR_API_KEY_NOT_VALID:
				throw new APIKeyNotValidException();
			case ERR_INVALID_FUNCTION:
				throw new InvalidFunctionException();
			case ERR_UNKNOWN:
				throw new SPIException();
			case ERR_VERSION:
				throw new InvalidVersionException();
			case ERR_INPUT:
				throw new InvalidInputException();
			default:
				throw e;
			}
		}
	}
	
}
