package com.commafeed.newsplus;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.springframework.http.converter.json.MappingJacksonHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import com.commafeed.newsplus.model.Category;
import com.commafeed.newsplus.model.Entries;
import com.commafeed.newsplus.model.FeedInfo;
import com.commafeed.newsplus.model.ServerInfo;
import com.commafeed.newsplus.model.Settings;
import com.commafeed.newsplus.model.Subscription;
import com.commafeed.newsplus.model.UnreadCount;
import com.commafeed.newsplus.model.UserModel;
import com.commafeed.newsplus.model.request.AddCategoryRequest;
import com.commafeed.newsplus.model.request.CategoryModificationRequest;
import com.commafeed.newsplus.model.request.CollapseRequest;
import com.commafeed.newsplus.model.request.FeedModificationRequest;
import com.commafeed.newsplus.model.request.IDRequest;
import com.commafeed.newsplus.model.request.MarkRequest;
import com.commafeed.newsplus.model.request.MultipleMarkRequest;
import com.commafeed.newsplus.model.request.StarRequest;
import com.commafeed.newsplus.model.request.SubscribeRequest;
import com.googlecode.androidannotations.annotations.rest.Get;
import com.googlecode.androidannotations.annotations.rest.Post;
import com.googlecode.androidannotations.annotations.rest.Rest;

@JsonIgnoreProperties(ignoreUnknown = true)
@Rest(converters = { MappingJacksonHttpMessageConverter.class })
public interface CommaFeedClient {

	public void setRootUrl(String rootUrl);

	RestTemplate getRestTemplate();

	void setRestTemplate(RestTemplate restTemplate);

	/*
	 * Category API
	 * ############
	 */

	@Post("/category/collapse")
	void categoryCollapse(CollapseRequest collapseRequest);

	@Get("/category/unreadCount")
	UnreadCount categoryUnreadCount();

	@Post("/category/mark")
	void categoryMark(MarkRequest markRequest);

	@Post("/category/add")
	Long categoryAdd(AddCategoryRequest addCategoryRequest);

	@Get("/category/entries?id={id}&readType={readType}&newerThan={newerThan}&offset={offset}&limit={limit}&order={order}&onlyIds={onlyIds}&excludedSubscriptionIds={excludedSubscriptionIds}")
	Entries categoryEntries(String id, String readType, long newerThan, int offset, int limit, String order, boolean onlyIds, String excludedSubscriptionIds);

	@Post("/category/delete")
	void categoryDelete(IDRequest idRequest);

	@Post("/category/modify")
	void categoryModify(CategoryModificationRequest categoryModificationRequest);

	@Get("/category/get")
	Category categoryGet();

	/*
	 * Server API
	 * ##########
	 */

	@Get("/server/get")
	ServerInfo serverGet();

	/*
	 * User API
	 * ########
	 */

	@Get("/user/settings")
	Settings userSettings();

	@Post("/user/settings")
	void userSettings(Settings settings);

	@Get("/user/profile")
	UserModel userProfile();

	@Post("/user/profile")
	void userProfile(UserModel userModel);

	@Post("/user/profile/deleteAccount")
	void userProfileDeleteAccount();

	/*
	 * Entry API
	 * #########
	 */

	@Post("/entry/mark")
	void entryMark(MarkRequest markRequest);

	@Post("/entry/markMultiple")
	void entryMarkMultiple(MultipleMarkRequest markRequest);

	@Post("/entry/star")
	void entryStar(StarRequest starRequest);

	@Get("/entry/search")
	Entries entrySearch();

	/*
	 * Feed API
	 * ########
	 */

	@Get("/feed/subscribe?url={url}")
	void feedSubscribe(String url);

	@Post("/feed/subscribe")
	void feedSubscribe(SubscribeRequest subscribeRequest);

	@Post("/feed/favicon/{id}")
	void feedFaviconId(Long id);

	@Post("/feed/modify")
	void feedModify(FeedModificationRequest feedModificationRequest);

	@Get("/feed/entries?id={id}&readType={readType}&newerThan={newerThan}&offset={offset}&limit={limit}&order={order}&onlyIds={onlyIds}")
	Entries feedEntries(String id, String readType, long newerThan, int offset, int limit, String order, boolean onlyIds);

	@Get("/feed/fetch?title={title}&url={url}")
	FeedInfo feedFetch(String title, String url);

	@Post("/feed/refresh")
	void feedRefresh(IDRequest idRequest);

	@Post("/feed/mark")
	void feedMark(MarkRequest markRequest);

	@Get("/feed/get/{id}")
	Subscription feedGet(long id);

	@Post("/feed/unsubscribe")
	void feedUnsubscribe(IDRequest idRequest);
}