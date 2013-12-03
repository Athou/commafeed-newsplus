package com.commafeed.newsplus;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.http.ContentCodingType;
import org.springframework.http.HttpAuthentication;
import org.springframework.http.HttpBasicAuthentication;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.util.CollectionUtils;

import android.content.Context;
import android.os.RemoteException;
import android.text.TextUtils;

import com.commafeed.newsplus.model.Category;
import com.commafeed.newsplus.model.Entries;
import com.commafeed.newsplus.model.Entry;
import com.commafeed.newsplus.model.Subscription;
import com.commafeed.newsplus.model.request.AddCategoryRequest;
import com.commafeed.newsplus.model.request.CategoryModificationRequest;
import com.commafeed.newsplus.model.request.FeedModificationRequest;
import com.commafeed.newsplus.model.request.IDRequest;
import com.commafeed.newsplus.model.request.MarkRequest;
import com.commafeed.newsplus.model.request.MultipleMarkRequest;
import com.commafeed.newsplus.model.request.StarRequest;
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

	private CommaFeedClient client;

	public CommaFeedExtension() {

	}

	public CommaFeedExtension(Context context) {
		init(context);
	}

	@Override
	public void onCreate() {
		super.onCreate();
		init(getApplicationContext());
	}

	private void init(final Context context) {
		super.onCreate();
		CommaFeedClient_ client = new CommaFeedClient_();
		client.setRootUrl(APIHelper.removeTrailingSlash(Prefs.getServer(context)) + "/rest");

		ClientHttpRequestInterceptor interceptor = new ClientHttpRequestInterceptor() {
			@Override
			public ClientHttpResponse intercept(HttpRequest req, byte[] body, ClientHttpRequestExecution execution) throws IOException {
				HttpHeaders headers = req.getHeaders();
				HttpAuthentication auth = new HttpBasicAuthentication(Prefs.getUserName(context), Prefs.getUserPassword(context));
				headers.setAuthorization(auth);
				headers.setAcceptEncoding(ContentCodingType.GZIP);
				return execution.execute(req, body);
			}
		};
		client.getRestTemplate().setInterceptors(Arrays.asList(interceptor));
		this.client = client;
	}

	/**
	 * Retrieve folders, subscriptions and unread count
	 */
	@Override
	public void handleReaderList(ITagListHandler tagHandler, ISubscriptionListHandler subHandler, long syncTime) throws IOException,
			ReaderException {
		Category root = client.categoryGet();
		List<ITag> tags = new ArrayList<ITag>();
		List<ISubscription> subs = new ArrayList<ISubscription>();
		handleCategory(root, tags, subs);

		ITag starredTag = new ITag();
		starredTag.uid = APIHelper.STARRED_TAG_ID;
		starredTag.label = APIHelper.STARRED_TAG_ID;
		starredTag.type = ITag.TYPE_TAG_STARRED;
		starredTag.unreadCount = 0;
		tags.add(starredTag);
		try {
			tagHandler.tags(tags);
			subHandler.subscriptions(subs);
		} catch (RemoteException e) {
			throw new ReaderException("data parse error", e);
		}
	}

	private int handleCategory(Category current, List<ITag> tags, List<ISubscription> subs) {
		int unreadCount = 0;
		for (Subscription sub : current.getFeeds()) {
			ISubscription isub = APIHelper.convertSubscription(sub);
			unreadCount += isub.unreadCount;
			subs.add(isub);
		}
		for (Category cat : current.getChildren()) {
			unreadCount += handleCategory(cat, tags, subs);
			tags.add(APIHelper.convertCategory(cat, unreadCount));
		}
		return unreadCount;
	}

	@Override
	public void handleItemIdList(IItemIdListHandler handler, long syncTime) throws IOException, ReaderException {
		try {
			String uid = handler.stream();
			String readType = handler.excludeRead() ? "unread" : "all";
			String order = handler.newestFirst() ? "desc" : "asc";
			int limit = handler.limit();
			long newerThan = handler.startTime();

			Entries entries = null;
			if (uid.startsWith(ReaderExtension.STATE_STARRED)) {
				entries = client.categoryEntries("starred", readType, newerThan, 0, limit, order, true, null);
			} else if (uid.startsWith(ReaderExtension.STATE_READING_LIST)) {
				entries = client.categoryEntries("all", readType, newerThan, 0, limit, order, true, null);
			} else if (APIHelper.isSubscription(uid)) {
				entries = client.feedEntries(APIHelper.convertID(uid, APIHelper.PREFIX_SUB), readType, newerThan, 0, limit, order, true);
			} else if (APIHelper.isCategory(uid)) {
				entries = client.categoryEntries(APIHelper.convertID(uid, APIHelper.PREFIX_CAT), readType, newerThan, 0, limit, order,
						true, null);
			} else {
				throw new ReaderException("Unknown reading state");
			}

			List<String> list = new ArrayList<String>();
			for (Entry entry : entries.getEntries()) {
				list.add(APIHelper.PREFIX_ENTRY + entry.getId());
			}
			handler.items(list);
		} catch (RemoteException e) {
			throw new ReaderException("ItemID handler error", e);
		}
	}

	@Override
	public void handleItemList(IItemListHandler handler, long syncTime) throws IOException, ReaderException {
		try {
			boolean hasMore = true;
			int offset = 0;
			while (hasMore && handler.continuation()) {
				Entries entries = handleItemList(handler, offset);
				hasMore = entries.isHasMore();
				offset += entries.getEntries().size();
			}
		} catch (RemoteException e) {
			throw new ReaderException("ItemID handler error", e);
		}
	}

	private Entries handleItemList(IItemListHandler handler, int offset) throws IOException, ReaderException, RemoteException {
		String uid = handler.stream();
		String readType = handler.excludeRead() ? "unread" : "all";
		String order = handler.newestFirst() ? "desc" : "asc";
		int limit = Math.min(handler.limit(), 50);
		long newerThan = handler.startTime();
		List<String> excludedStreams = handler.excludedStreams();

		String excludedSubscriptionIds = null;
		if (!CollectionUtils.isEmpty(excludedStreams)) {
			List<String> converted = new ArrayList<String>();
			for (String excludedStream : excludedStreams) {
				converted.add(APIHelper.convertID(excludedStream, APIHelper.PREFIX_SUB));
			}
			excludedSubscriptionIds = TextUtils.join(",", converted);
		}

		Entries entries = null;
		if (uid.startsWith(APIHelper.STARRED_TAG_ID)) {
			entries = client.categoryEntries("starred", readType, newerThan, offset, limit, order, false, excludedSubscriptionIds);
		} else if (uid.startsWith(ReaderExtension.STATE_READING_LIST)) {
			entries = client.categoryEntries("all", readType, newerThan, offset, limit, order, false, excludedSubscriptionIds);
		} else if (APIHelper.isSubscription(uid)) {
			entries = client.feedEntries(APIHelper.convertID(uid, APIHelper.PREFIX_SUB), readType, newerThan, offset, limit, order, false);
		} else if (APIHelper.isCategory(uid)) {
			entries = client.categoryEntries(APIHelper.convertID(uid, APIHelper.PREFIX_CAT), readType, newerThan, offset, limit, order,
					false, excludedSubscriptionIds);
		} else {
			throw new ReaderException("Unknown reading state");
		}

		int count = 0;
		int length = 0;
		List<IItem> items = new ArrayList<IItem>();
		for (IItem item : APIHelper.convertEntries(entries.getEntries())) {
			count++;
			length += item.getLength();
			items.add(item);
			if (count == 200 || length > 300000) {
				// prevent TransactionTooLargeException, android only allows 1mb per transaction
				handler.items(items, STRATEGY_INSERT_DEFAULT);
				count = 0;
				length = 0;
				items.clear();
			}
		}
		handler.items(items, STRATEGY_INSERT_DEFAULT);
		return entries;
	}

	@Override
	public boolean markAllAsRead(String subId, String tagId, String[] excludedSubs, long syncTime) throws IOException, ReaderException {
		MarkRequest req = new MarkRequest();
		req.setOlderThan(syncTime * 1000);
		if (subId == null) {
			req.setId(tagId == null ? "all" : APIHelper.convertID(tagId, APIHelper.PREFIX_CAT));
			if (excludedSubs != null) {
				List<Long> excludedIds = new ArrayList<Long>();
				for (String uid : excludedSubs) {
					excludedIds.add(Long.valueOf(APIHelper.convertID(uid, APIHelper.PREFIX_SUB)));
				}
				req.setExcludedSubscriptions(excludedIds);
			}
			client.categoryMark(req);
		} else {
			req.setId(APIHelper.convertID(subId, APIHelper.PREFIX_SUB));
			client.feedMark(req);
		}
		return true;
	}

	@Override
	public boolean markAsRead(String[] itemUids, String[] subUids) throws IOException, ReaderException {
		MultipleMarkRequest mmr = new MultipleMarkRequest();
		for (int i = 0; i < itemUids.length; i++) {
			MarkRequest req = new MarkRequest();
			req.setId(APIHelper.convertID(itemUids[i], APIHelper.PREFIX_ENTRY));
			req.setRead(true);
			mmr.getRequests().add(req);
		}
		client.entryMarkMultiple(mmr);
		return true;
	}

	@Override
	public boolean markAsUnread(String[] itemUids, String[] subUids, boolean keepUnread) throws IOException, ReaderException {
		MultipleMarkRequest mmr = new MultipleMarkRequest();
		for (int i = 0; i < itemUids.length; i++) {
			MarkRequest req = new MarkRequest();
			req.setId(APIHelper.convertID(itemUids[i], APIHelper.PREFIX_ENTRY));
			req.setRead(false);
			mmr.getRequests().add(req);
		}
		client.entryMarkMultiple(mmr);
		return true;
	}

	/**
	 * star or unstar items
	 */
	@Override
	public boolean editItemTag(String[] itemUids, String[] subUids, String[] tags, int action) throws IOException, ReaderException {
		for (int i = 0; i < itemUids.length; i++) {
			if (tags != null) {
				
				for (String tag : tags) {
					if (tag.startsWith(APIHelper.STARRED_TAG_ID)) {
						StarRequest sr = new StarRequest();
						sr.setId(APIHelper.convertID(itemUids[i], APIHelper.PREFIX_ENTRY));
						sr.setFeedId(Long.valueOf(APIHelper.convertID(subUids[i], APIHelper.PREFIX_SUB)));
						sr.setStarred(action != ACTION_ITEM_TAG_REMOVE_LABEL);
						client.entryStar(sr);
					} else {
						// TODO handle tags
					}
				}
			}
		}
		return true;
	}

	/**
	 * rename a category
	 */
	@Override
	public boolean renameTag(String tagUid, String oldLabel, String newLabel) throws IOException, ReaderException {
		CategoryModificationRequest req = new CategoryModificationRequest();
		req.setId(Long.valueOf(APIHelper.convertID(tagUid, APIHelper.PREFIX_CAT)));
		req.setName(newLabel);
		client.categoryModify(req);
		return true;
	}

	/**
	 * delete a category
	 */
	@Override
	public boolean disableTag(String tagUid, String label) throws IOException, ReaderException {
		IDRequest req = new IDRequest();
		req.setId(Long.valueOf(APIHelper.convertID(tagUid, APIHelper.PREFIX_CAT)));
		client.categoryDelete(req);
		return true;
	}

	/**
	 * add, delete, rename a subscription or change its category
	 */
	@Override
	public boolean editSubscription(String uid, String title, String url, String[] tags, int action) throws IOException,
			ReaderException {

		Long id = Long.valueOf(APIHelper.convertID(uid, APIHelper.PREFIX_SUB));
		String categoryId = null;
		if (tags != null && tags.length > 0) {
			categoryId = APIHelper.convertID(tags[0], APIHelper.PREFIX_CAT);
		}

		if (action == ReaderExtension.ACTION_SUBSCRIPTION_SUBCRIBE) {
			SubscribeRequest sr = new SubscribeRequest();
			sr.setUrl(url);
			sr.setTitle(title);
			sr.setCategoryId(categoryId);
			client.feedSubscribe(sr);
		} else if (action == ReaderExtension.ACTION_SUBSCRIPTION_UNSUBCRIBE) {
			IDRequest idr = new IDRequest();
			idr.setId(id);
			client.feedUnsubscribe(idr);
		} else if (action == ReaderExtension.ACTION_SUBSCRIPTION_EDIT) {
			FeedModificationRequest fmr = new FeedModificationRequest();
			fmr.setId(id);
			fmr.setName(title);
			fmr.setCategoryId(categoryId);
			client.feedModify(fmr);
		} else if (action == ReaderExtension.ACTION_SUBSCRIPTION_NEW_LABEL || action == ReaderExtension.ACTION_SUBSCRIPTION_ADD_LABEL) {
			if (action == ReaderExtension.ACTION_SUBSCRIPTION_NEW_LABEL) {
				AddCategoryRequest req = new AddCategoryRequest();
				req.setName(tags[0]);
				categoryId = String.valueOf(client.categoryAdd(req));
			}
			FeedModificationRequest fmr = new FeedModificationRequest();
			fmr.setId(id);
			fmr.setName(title);
			fmr.setCategoryId(categoryId);
			client.feedModify(fmr);
		} else if (action == ReaderExtension.ACTION_SUBSCRIPTION_REMOVE_LABEL) {
			FeedModificationRequest fmr = new FeedModificationRequest();
			fmr.setId(id);
			fmr.setName(title);
			fmr.setCategoryId(null);
			client.feedModify(fmr);
		}

		return true;
	}

	/**
	 * pings the server using authentication
	 */
	public boolean ping() throws IOException, ReaderException {
		return client.serverGet() != null;
	}

}
