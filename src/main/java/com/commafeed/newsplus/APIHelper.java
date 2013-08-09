package com.commafeed.newsplus;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.commafeed.newsplus.model.Category;
import com.commafeed.newsplus.model.Entry;
import com.commafeed.newsplus.model.Subscription;
import com.noinnion.android.reader.api.provider.IItem;
import com.noinnion.android.reader.api.provider.ISubscription;
import com.noinnion.android.reader.api.provider.ITag;

public class APIHelper {

	public static final String PREFIX_ENTRY = "entry:";
	public static final String PREFIX_SUB = "sub:";
	public static final String PREFIX_CAT = "cat:";

	public static final String STARRED_TAG_ID = "##starred##";

	public static String convertID(String uid, String prefix) {
		return uid.substring(prefix.length());
	}

	public static boolean isSubscription(String uid) {
		return uid.startsWith(PREFIX_SUB);
	}

	public static boolean isCategory(String uid) {
		return uid.startsWith(PREFIX_CAT);
	}

	public static boolean isEntry(String uid) {
		return uid.startsWith(PREFIX_ENTRY);
	}

	public static ITag convertCategory(Category cat, int unreadCount) {
		ITag itag = new ITag();
		itag.id = Long.valueOf(cat.getId());
		itag.uid = PREFIX_CAT + cat.getId();
		itag.label = cat.getName();
		itag.type = ITag.TYPE_FOLDER;
		itag.sortid = String.valueOf(cat.getPosition());
		itag.unreadCount = unreadCount;
		return itag;
	}

	public static ISubscription convertSubscription(Subscription sub) {
		ISubscription isub = new ISubscription();
		isub.id = sub.getId();
		isub.uid = PREFIX_SUB + sub.getId();
		isub.htmlUrl = sub.getFeedLink();
		isub.title = sub.getName();
		isub.unreadCount = (int) sub.getUnread();
		isub.sortid = String.valueOf(sub.getPosition());
		isub.newestItemTime = sub.getNewestItemTime().getTime();
		return isub;
	}

	public static List<IItem> convertEntries(List<Entry> entries) {
		List<IItem> items = new ArrayList<IItem>();
		for (Entry entry : entries) {
			items.add(convertEntry(entry));
		}
		return items;
	}

	public static IItem convertEntry(Entry entry) {
		IItem item = new IItem();
		item.id = Long.valueOf(entry.getId());
		item.uid = PREFIX_ENTRY + entry.getId();
		item.author = entry.getAuthor();
		item.content = entry.getContent();
		item.forceUpdate = false;
		item.link = entry.getUrl();
		item.media = entry.getEnclosureUrl();
		item.mediaType = entry.getEnclosureType();
		item.publishedTime = entry.getDate().getTime();
		item.read = entry.isRead();
		item.readTime = new Date().getTime();
		item.starred = entry.isStarred();
		item.subUid = PREFIX_SUB + entry.getFeedId();
		item.title = entry.getTitle();
		item.updatedTime = entry.getInsertedDate().getTime();
		if (entry.isStarred()) {
			item.addCategory(STARRED_TAG_ID);
		}
		return item;
	}
	
	public static String removeTrailingSlash(String url) {
		if (url.endsWith("/")) {
			url = url.substring(0, url.length() - 1);
		}
		return url;
	}

}
