package com.eryansky.webservice.soap.server;

import javax.jws.WebParam;
import javax.jws.WebService;

import com.eryansky.webservice.soap.server.result.GetUserResult;



/**
 * JAX-WS2.0的WebService接口定义类.
 * @author 尔演&Eryan eryanwcp@gmail.com
 * @date 2013-3-11 下午9:25:00 
 *
 */
@WebService(name = "UserService", targetNamespace = WsConstants.NS)
public interface UserWebService {

	/**
	 * 获取用户, 受SpringSecurity保护.
	 */
	GetUserResult getUser(@WebParam(name = "name") String name);
}
