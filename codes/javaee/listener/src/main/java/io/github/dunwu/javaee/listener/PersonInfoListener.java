package io.github.dunwu.javaee.listener;

import java.io.Serializable;
import java.util.Date;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionActivationListener;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionBindingListener;
import javax.servlet.http.HttpSessionEvent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PersonInfoListener implements HttpSessionActivationListener,
		HttpSessionBindingListener, Serializable {

	private static final long serialVersionUID = -4780592776386225973L;

	Logger log = LoggerFactory.getLogger(getClass());

	private String name;

	private Date dateCreated;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Date getDateCreated() {
		return dateCreated;
	}

	public void setDateCreated(Date dateCreated) {
		this.dateCreated = dateCreated;
	}

	// 从硬盘加载后
	public void sessionDidActivate(HttpSessionEvent se) {
		HttpSession session = se.getSession();
		log.info(this + "已经成功从硬盘中加载。sessionId: " + session.getId());
	}

	// 即将被钝化到硬盘时
	public void sessionWillPassivate(HttpSessionEvent se) {
		HttpSession session = se.getSession();
		log.info(this + "即将保存到硬盘。sessionId: " + session.getId());
	}

	// 被放进session前
	public void valueBound(HttpSessionBindingEvent event) {
		HttpSession session = event.getSession();
		String name = event.getName();
		log.info(this + "被绑定到session \"" + session.getId() + "\"的" + name
				+ "属性上");

		// 记录放到session中的时间
		this.setDateCreated(new Date());
	}

	// 从session中移除后
	public void valueUnbound(HttpSessionBindingEvent event) {
		HttpSession session = event.getSession();
		String name = event.getName();
		log.info(this + "被从session \"" + session.getId() + "\"的" + name
				+ "属性上移除");
	}

	@Override
	public String toString() {
		return "PersonInfoListener(" + name + ")";
	}

}
