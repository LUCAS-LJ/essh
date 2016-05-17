package com.eryansky.core.security;

import com.google.common.collect.Maps;

import javax.servlet.http.HttpSession;
import java.util.Map;

/**
 * 应用Session上下文
 */
public class ApplicationSessionContext {

	private static ApplicationSessionContext instance;

	/**
	 * 用于存储HttpSession的Map对象
	 */
	private Map<String,HttpSession> sessionData;

	private ApplicationSessionContext() {
		sessionData = Maps.newHashMap();
	}

	public static ApplicationSessionContext getInstance() {
		if (instance == null) {
			instance = new ApplicationSessionContext();
		}
		return instance;
	}

	public synchronized void addSession(HttpSession session) {
		if (session != null) {
			sessionData.put(session.getId(), session);
		}
	}

	public synchronized void removeSession(HttpSession session) {
		if (session != null) {
			sessionData.remove(session.getId());
		}
	}

	public synchronized HttpSession getSession(String sessionId) {
		if (sessionId == null) return null;
		return (HttpSession) sessionData.get(sessionId);
	}

	public Map<String,HttpSession> getSessionData() {
		return sessionData;
	}
}