package com.mr.webiter;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.ModelDriven;
import com.mr.dao.ObjectDao;
import com.mr.model.ArticleInfo;
import com.mr.model.ReArticleInfo;
import com.mr.model.UserInfo;

public class ArticleAction extends ActionSupport implements
		ModelDriven<ArticleInfo>, ServletRequestAware {
	private String hql;

	private ArticleInfo articleInfo = new ArticleInfo();

	protected HttpServletRequest request;

	private ObjectDao<ArticleInfo> objectDao = null;

	String dateTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
			.format(Calendar.getInstance().getTime());

	public ArticleInfo getModel() {
		return articleInfo;
	}

	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
	}

	// ¹ÜÀíÔ±µÇÂ¼ºó£¬¶ÔÎÄÕÂµÄÏêÏ¸²éÑ¯
	public String admin_articleQueryOne() {
		objectDao = new ObjectDao<ArticleInfo>();//ÊµÀý»¯³Ö¾Ã»¯¶ÔÏó
		hql = "select author from ArticleInfo group by author";//²éÑ¯ÎÄÕÂÏêÏ¸ÐÅÏ¢µÄhqlÓï¾ä
		List authorList = objectDao.queryListObject(hql);//Ö´ÐÐ²éÑ¯
		request.setAttribute("authorList", authorList);
		Integer id = new Integer(request.getParameter("id"));//»ñÈ¡ÎÄÕÂid
		hql = "from ArticleInfo where id = " + id;//¸ù¾Ýid²éÑ¯ÎÄÕÂÏêÏ¸ÐÅÏ¢µÄhqlÓï¾ä
		articleInfo = objectDao.queryFrom(hql);//Ö´ÐÐ²éÑ¯
		if (null != request.getParameter("commend")) {//ÐÞ¸ÄÎÄÕÂµÄÍÆ¼ö×´Ì¬
			if (articleInfo.getCommend().equals("·ñ")) {
				articleInfo.setCommend("ÊÇ");
			} else {
				articleInfo.setCommend("·ñ");
			}
			objectDao.updateT(articleInfo);//ÐÞ¸ÄÎÄÕÂµÄÍÆ¼ö×´Ì¬
		}
		articleInfo = objectDao.queryFrom(hql);//ÔÙ´Î²éÑ¯ÎÄÕÂÏêÏ¸ÐÅÏ¢
		request.setAttribute("articleInfo", articleInfo);
		return "admin_articleQueryOne";
	}

	// ¹ÜÀíÔ±µÇÂ¼ºó£¬ÎÄÕÂ²éÑ¯²Ù×÷
	public String admin_articleQuery() {
		// 以下是对文章的全部查询
		hql = "from ArticleInfo";//设置对文章全部查询HQL语句
		String account = request.getParameter("account");
		
	
		if (null != account) {
			try {
				account = new String(account.getBytes("ISO8859_1"),"gbk");
				System.out.println("ssssssss "+account);
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			hql = "from ArticleInfo where author = '" + account + "'";
			request.setAttribute("account", account);
		}
		objectDao = new ObjectDao<ArticleInfo>();
		List<ArticleInfo> list = objectDao.queryList(hql);
		
		int showNumber = 10;
		Integer count = 0;
		if (null != request.getParameter("count")) {
			count = new Integer(request.getParameter("count"));
		}
		list = objectDao.queryList(hql);
		int maxPage = list.size();
		if (maxPage % showNumber == 0) {
			maxPage = maxPage / showNumber;
		} else {
			maxPage = maxPage / showNumber + 1;
		}
		if (0 == count) {
			list = objectDao.queryList(hql, showNumber, count);
		} else {
			count--;
			list = objectDao.queryList(hql, showNumber, count * showNumber);
		}
		request.setAttribute("count", count);
		request.setAttribute("list", list);
		request.setAttribute("maxPage", maxPage);
		
		hql = "select author from ArticleInfo group by author";
		List authorList = objectDao.queryListObject(hql);
		request.setAttribute("authorList", authorList);
		return "admin_articleQuery";
	}

	// Ç°Ì¨ÎÄÕÂµÄÏêÏ¸²éÑ¯
	public String f_article_query() {
		// ÎÄÕÂµÄÏêÏ¸²éÑ¯
		Integer id = new Integer(request.getParameter("id"));//»ñÈ¡id²ÎÊý
		hql = "from ArticleInfo where id =" + id;//¸ù¾Ýid²ÎÊý²éÑ¯µÄhqlÓï¾ä
		objectDao = new ObjectDao<ArticleInfo>();//ÊµÀý»¯³Ö¾Ã»¯Àà
		articleInfo = objectDao.queryFrom(hql);//Ö´ÐÐ²éÑ¯
		String account = (String) request.getSession().getAttribute("account");//»ñÈ¡account²ÎÊý
		if(null==account){//Èç¹û	account¶ÔÏóÎª¿Õ
			account=articleInfo.getAuthor();//»ñÈ¡ÓÃ»§Ãû
			hql = "from UserInfo where account = '" + account + "'";//¸ù¾ÝÓÃ»§Ãû²éÑ¯µÄhqlÓï¾ä
			ObjectDao<UserInfo>	objectDao1 = new ObjectDao<UserInfo>();//ÊµÀý»¯³Ö¾Ã»¯Àà
			UserInfo userInfo = objectDao1.queryFrom(hql);//Ö´ÐÐ²éÑ¯
			request.getSession().setAttribute("userInfo", userInfo);//½«²éÑ¯½á¹û±£´æÔÚSessionÖÐ
		}
		if (null == request.getParameter("count")) {
			if (!articleInfo.getAuthor().equals(account)) {
				articleInfo.setVisit(articleInfo.getVisit() + 1);
				objectDao.updateT(articleInfo);
			}
		}
		request.setAttribute("articleInfo", articleInfo);//±£´æarticleInfo
		// ÎÄÕÂ»Ø¸´ÄÚÈÝµÄÏêÏ¸²éÑ¯
		hql = "from ReArticleInfo where re_id=" + id + " order by id desc";//²éÑ¯ÎÄÕÂÏêÏ¸ÄÚÈÝµÄhqlÓï¾ä
		ObjectDao<ReArticleInfo> re_objectDao = new ObjectDao<ReArticleInfo>();//ÊµÀý»¯³Ö¾Ã»¯Àà
		List<ReArticleInfo> list = null;//¶¨ÒåList¼¯ºÏ
		// ·ÖÒ³²Ù×÷
		int showNumber = 3;
		Integer count = 0;
		if (null != request.getParameter("count")) {
			count = Integer.valueOf(request.getParameter("count"));
		}
		list = re_objectDao.queryList(hql);
		if(list.size()!=0){
		int maxPage = list.size();
		if (maxPage % showNumber == 0) {
			maxPage = maxPage / showNumber;
		} else {
			maxPage = maxPage / showNumber + 1;
		}
		if (0 == count) {
			list = re_objectDao.queryList(hql, showNumber, count);
		} else {
			count--;
			list = re_objectDao.queryList(hql, showNumber, count * showNumber);
		}
		request.setAttribute("count", count);
		request.setAttribute("list", list);
		request.setAttribute("maxPage", maxPage);
		}	
		return "f_article_query";
	}

	public String article_forwardUpdate() {
		objectDao = new ObjectDao<ArticleInfo>();
		Integer id = new Integer(request.getParameter("id"));
		hql = "from ArticleInfo where id =" + id;
		articleInfo = objectDao.queryFrom(hql);
		request.setAttribute("articleInfo", articleInfo);
		return "article_forwardUpdate";
	}

	public void validateArticle_update() {
		if (articleInfo.getTitle().equals("")) {
			this.addFieldError("title", "ÇëÊäÈëÎÄÕÂ±êÌâ£¡<br><br>");
		}
		if (articleInfo.getContent().equals("")) {
			this.addFieldError("content", "ÇëÊäÈëÎÄÕÂÄÚÈÝ£¡");
		}

	}

	// ÎÄÕÂÐÞ¸Ä²Ù×÷
	public String article_update() {
		objectDao = new ObjectDao<ArticleInfo>();
		this.articleInfo.setSendTime(this.dateTimeFormat);
		String result = "ÐÞ¸ÄÎÄÕÂ³É¹¦£¡";
		if (!objectDao.updateT(articleInfo)) {
			result = "ÐÞ¸ÄÎÄÕÂÊ§°Ü£¡";
		}
		request.setAttribute("result", result);
		request.setAttribute("sign", "10");
		return "operationArticle";

	}

	// ÎÄÕÂÉ¾³ý²Ù×÷
	public String article_delete() {
		Integer id = new Integer(request.getParameter("id"));
		hql = "from ArticleInfo where id =" + id;
		objectDao = new ObjectDao<ArticleInfo>();
		articleInfo = objectDao.queryFrom(hql);
		String hql1 = "from ReArticleInfo where re_id =" + id + "";
		ObjectDao<ReArticleInfo> objectDao1 = new ObjectDao<ReArticleInfo>();
		List<ReArticleInfo> list = objectDao1.queryList(hql1);
		for (ReArticleInfo reArticleInfo : list) {
			objectDao1.deleteT(reArticleInfo);
		}
		boolean flag = objectDao.deleteT(articleInfo);
		String result = "É¾³ýÎÄÕÂ³É¹¦£¡";
		if (!flag) {
			result = "É¾³ýÎÄÕÂÊ§°Ü£¡";
		}
		request.setAttribute("result", result);
		request.setAttribute("sign", "10");
		return "operationArticle";
	}

	// ÎÄÕÂÏêÏ¸²éÑ¯
	public String article_queryContent() {
		Integer id = new Integer(request.getParameter("id"));
		hql = "from ArticleInfo where id =" + id;
		objectDao = new ObjectDao<ArticleInfo>();
		articleInfo = objectDao.queryFrom(hql);
		request.setAttribute("form", articleInfo);
		return SUCCESS;
	}

	// ÎÄÕÂ²éÑ¯²Ù×÷
	public String article_query() {
		String account = (String) request.getSession().getAttribute("account");
		hql = "from ArticleInfo where author='" + account
				+ "' order by id desc";
		objectDao = new ObjectDao<ArticleInfo>();
		List<ArticleInfo> list = null;
		Integer showNumber = 15;
		Integer count = 0;
		if (null != request.getParameter("count")) {
			count = Integer.valueOf(request.getParameter("count"));
		}
		list = objectDao.queryList(hql);
		Integer maxPage = list.size();
		if (maxPage % showNumber == 0) {
			maxPage = maxPage / showNumber;
		} else {
			maxPage = maxPage / showNumber + 1;
		}
		if (0 == count) {
			list = objectDao.queryList(hql, showNumber, count);
		} else {
			count--;
			list = objectDao.queryList(hql, showNumber, count * showNumber);
		}
		request.setAttribute("count", count);
		request.setAttribute("list", list);
		request.setAttribute("maxPage", maxPage);
		return SUCCESS;
	}

	// ÎÄÕÂ±íµ¥µÄÐ£Ñé
	public void validateArticle_add() {
		if (articleInfo.getTitle().equals("")) {
			this.addFieldError("title", "ÇëÊäÈëÎÄÕÂ±êÌâ£¡<br><br>");
		}
		if (articleInfo.getContent().equals("")) {
			this.addFieldError("content", "ÇëÊäÈëÎÄÕÂÄÚÈÝ£¡");
		}

	}

	// ÎÄÕÂÌí¼Ó
	public String article_add() {
		objectDao = new ObjectDao<ArticleInfo>();
		this.articleInfo.setSendTime(this.dateTimeFormat);
		if (objectDao.saveT(articleInfo)) {
			request.setAttribute("result", "Ìí¼ÓÎÄÕÂ³É¹¦£¡");
		} else {
			request.setAttribute("result", "Ìí¼ÓÎÄÕÂÊ§°Ü£¡");
		}
		return SUCCESS;
	}
}
