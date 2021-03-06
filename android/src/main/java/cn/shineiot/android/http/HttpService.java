package cn.shineiot.android.http;

import cn.shineiot.android.bean.AndroidNews;
import cn.shineiot.android.bean.Banner;
import cn.shineiot.android.bean.SearchEntity;
import cn.shineiot.base.bean.Collect;
import cn.shineiot.base.http.HttpClient;
import cn.shineiot.base.module.BaseListResult;
import cn.shineiot.base.module.BaseResult;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import rx.Observable;

/**
 * @author GF63
 */
public interface HttpService {

	Http HTTP = HttpClient.getInstace().create(Http.class);

	interface Http {

		/**
		 * banner
		 */
		@GET("banner/json")
		Observable<BaseListResult<Banner>> getBanner();

		/**
		 * Android 文章
		 */
		@GET("article/list/{page}/json")
		Observable<BaseResult<AndroidNews>> getAndroidNews(@Path("page")int page);

		/**
		 * 收藏文章
		 * @param id
		 * @return
		 */
		@POST("lg/collect/{id}/json")
		Observable<BaseResult> collect(@Path("id")int id);

		/**
		 * 取消收藏
		 * @param id
		 * @return
		 */
		@POST("lg/uncollect_originId/{id}/json")
		Observable<BaseResult> unCollect(@Path("id")int id);

		/**
		 * 我的收藏
		 */
		@GET("lg/collect/list/0/json")
		Observable<BaseResult<Collect>> getMyCollect();

		/**
		 * 搜索
		 */
		@FormUrlEncoded
		@POST("article/query/{page}/json")
		Observable<BaseResult<AndroidNews>> searchData(@Field("k")String k,@Path("page")int page);
	}
}
