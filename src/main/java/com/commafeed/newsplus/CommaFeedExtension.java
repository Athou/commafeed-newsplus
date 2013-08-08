package com.commafeed.newsplus;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.http.HttpAuthentication;
import org.springframework.http.HttpBasicAuthentication;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import android.content.Context;
import android.os.RemoteException;

import com.commafeed.newsplus.model.Category;
import com.commafeed.newsplus.model.Entries;
import com.commafeed.newsplus.model.Entry;
import com.commafeed.newsplus.model.Subscription;
import com.commafeed.newsplus.model.request.FeedModificationRequest;
import com.commafeed.newsplus.model.request.IDRequest;
import com.commafeed.newsplus.model.request.MarkRequest;
import com.commafeed.newsplus.model.request.MultipleMarkRequest;
import com.commafeed.newsplus.model.request.SubscribeRequest;
import com.noinnion.android.reader.api.ReaderException;
import com.noinnion.android.reader.api.ReaderExtension;
import com.noinnion.android.reader.api.internal.IItemIdListHandler;
import com.noinnion.android.reader.api.internal.IItemListHandler;
import com.noinnion.android.reader.api.internal.ISubscriptionListHandler;
import com.noinnion.android.reader.api.internal.ITagListHandler;
import com.noinnion.android.reader.api.provider.IItem;
import com.noinnion.android.reader.api.provider.ISubscription;
import com.noinnion.android.reader.api.provider.ITag;

public class CommaFeedExtension extends ReaderExtension {

	private static final String PREFIX_SUB = "sub:";
	private static final String PREFIX_CAT = "cat:";

	protected Context context;
	private CommaFeedClient client;

	public CommaFeedExtension(final Context context) {
		this.context = context;
		CommaFeedClient_ client = new CommaFeedClient_();
		client.setRootUrl(Prefs.getServer(context) + "/rest/");
		client.getRestTemplate().getInterceptors().add(new ClientHttpRequestInterceptor() {

			@Override
			public ClientHttpResponse intercept(HttpRequest req, byte[] body, ClientHttpRequestExecution execution) throws IOException {
				HttpHeaders headers = req.getHeaders();
				HttpAuthentication auth = new HttpBasicAuthentication(Prefs.getUserName(context), Prefs.getUserPassword(context));
				headers.setAuthorization(auth);
				return execution.execute(req, body);
			}
		});
		this.client = client;
	}

	@Override
	public boolean editSubscription(String uid, String title, String url, String[] tags, int action, long syncTime) throws IOException,
			ReaderException {

		Long id = Long.valueOf(uid.substring(PREFIX_SUB.length()));
		String categoryId = null;
		if (tags.length > 0) {
			categoryId = tags[0].substring(PREFIX_CAT.length());
		}

		switch (action) {
		case ReaderExtension.SUBSCRIPTION_ACTION_EDIT:
			FeedModificationRequest fmr = new FeedModificationRequest();
			fmr.setId(id);
			fmr.setName(title);
			fmr.setCategoryId(categoryId);
			client.feedModify(fmr);
			break;
		case ReaderExtension.SUBSCRIPTION_ACTION_ADD_LABEL:
			// do nothing
			break;
		case ReaderExtension.SUBSCRIPTION_ACTION_REMOVE_LABEL:
			// do nothing
			break;
		case ReaderExtension.SUBSCRIPTION_ACTION_SUBCRIBE:
			SubscribeRequest sr = new SubscribeRequest();
			sr.setUrl(url);
			sr.setTitle(title);
			sr.setCategoryId(categoryId);
			client.feedSubscribe(sr);
			break;
		case ReaderExtension.SUBSCRIPTION_ACTION_UNSUBCRIBE:
			IDRequest idr = new IDRequest();
			idr.setId(id);
			client.feedUnsubscribe(idr);
			break;
		}
		return false;
	}

	@Override
	public void handleItemIdList(IItemIdListHandler handler, long syncTime) throws IOException, ReaderException {
		// TODO
	}

	@Override
	public void handleItemList(IItemListHandler handler, long syncTime) throws IOException, ReaderException {
		try {
			String id = handler.stream();
			String readType = handler.excludeRead() ? "unread" : "all";
			String order = handler.newestFirst() ? "desc" : "asc";
			int limit = handler.limit();
			long newerThan = handler.startTime();

			Entries entries = null;
			if (id.startsWith(PREFIX_SUB)) {
				id = id.substring(PREFIX_SUB.length());
				entries = client.feedEntries(id, readType, newerThan, 0, limit, order);

			} else {
				id = id.substring(PREFIX_CAT.length());
				entries = client.categoryEntries(id, readType, newerThan, 0, limit, order);
			}

			List<IItem> items = new ArrayList<IItem>();
			for (Entry entry : entries.getEntries()) {
				IItem item = new IItem();
				item.id = Long.valueOf(entry.getId());
				item.uid = entry.getId();
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
				items.add(item);
			}
			// TODO handle paging ("continuation")
			handler.items(items, INSERT_STRATEGY_DEFAULT);
		} catch (RemoteException e) {
			throw new ReaderException("data parse error", e);
		}
	}

	@Override
	public void handleReaderList(ITagListHandler tagHandler, ISubscriptionListHandler subHandler, long syncTime) throws IOException,
			ReaderException {

		Category root = client.categoryGet();
		List<ITag> tags = new ArrayList<ITag>();
		List<ISubscription> subs = new ArrayList<ISubscription>();
		handleCategory(root, tags, subs);

		try {
			tagHandler.tags(new ArrayList<ITag>());
			subHandler.subscriptions(new ArrayList<ISubscription>());
		} catch (RemoteException e) {
			throw new ReaderException("data parse error", e);
		}
	}

	private int handleCategory(Category current, List<ITag> tags, List<ISubscription> subs) {
		int unreadCount = 0;
		for (Subscription sub : current.getFeeds()) {
			ISubscription isub = new ISubscription();
			isub.id = sub.getId();
			isub.uid = PREFIX_SUB + sub.getId();
			isub.htmlUrl = sub.getFeedUrl();
			isub.title = sub.getName();
			isub.unreadCount = (int) sub.getUnread();
			isub.sortid = String.valueOf(sub.getPosition());
			isub.newestItemTime = sub.getNewestItemTime().getTime();

			unreadCount += isub.unreadCount;
			subs.add(isub);
		}
		for (Category cat : current.getChildren()) {
			ITag tag = new ITag();
			unreadCount += handleCategory(cat, tags, subs);
			tag.id = Long.valueOf(cat.getId());
			tag.uid = PREFIX_CAT + cat.getId();
			tag.label = cat.getName();
			tag.type = ITag.TYPE_FOLDER;
			tag.sortid = String.valueOf(cat.getPosition());
			tag.unreadCount = unreadCount;

			tags.add(tag);
		}
		return unreadCount;
	}

	@Override
	public boolean disableTag(String tagUid, String label) throws IOException, ReaderException {
		// TODO
		return false;
	}

	@Override
	public boolean editItemTag(String[] itemUids, String[] subUids, String[] addTags, String[] removeTags) throws IOException,
			ReaderException {
		// TODO
		return false;
	}

	@Override
	public boolean renameTag(String tagUid, String oldLabel, String newLabel) throws IOException, ReaderException {
		// TODO
		return false;
	}

	@Override
	public boolean markAllAsRead(String s, String t, long syncTime) throws IOException, ReaderException {
		if (s == null) {
			s = "all";
			t = "all";
		}
		// TODO check what s and t mean
		MarkRequest req = new MarkRequest();
		req.setId(s);

		client.categoryMark(req);
		return true;
	}

	@Override
	public boolean markAsRead(String[] itemUids, String[] subUids) throws IOException, ReaderException {
		MultipleMarkRequest mmr = new MultipleMarkRequest();
		for (int i = 0; i < itemUids.length; i++) {
			MarkRequest req = new MarkRequest();
			req.setFeedId(Long.valueOf(subUids[i]));
			req.setId(itemUids[i]);
			req.setRead(true);
		}
		client.entryMarkMultiple(mmr);
		return true;
	}

	@Override
	public boolean markAsUnread(String[] itemUids, String[] subUids, boolean keepUnread) throws IOException, ReaderException {
		MultipleMarkRequest mmr = new MultipleMarkRequest();
		for (int i = 0; i < itemUids.length; i++) {
			MarkRequest req = new MarkRequest();
			req.setFeedId(Long.valueOf(subUids[i]));
			req.setId(itemUids[i]);
			req.setRead(false);
		}
		client.entryMarkMultiple(mmr);
		return false;
	}

	public boolean ping() throws IOException, ReaderException {
		return client.serverGet() != null;
	}

}
