package com.lib.lib_core.net.http.handler

import android.os.Handler
import android.text.TextUtils
import com.google.gson.GsonBuilder
import com.google.gson.JsonParser
import com.lib.lib_core.comm.utils.LogUtils
import com.lib.lib_core.net.http.HttpContext
import com.lib.lib_core.net.http.OkHttpClientHelper
import com.lib.lib_core.net.http.RequestListener
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.io.IOException
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * Created by lis on 2018/12/14.
 */
class DataSenderFilter : RequestHandler {
	// private static final String TAG = DataSenderFilter.class.getSimpleName();
	//    private static final int TIME_OUT = 10;
	//
	//    private static OkHttpClient okHttpClient;
	//
	//    public static OkHttpClient getOkHttpClient() {
	//        synchronized (DataSenderFilter.class) {
	//            if (okHttpClient == null) {
	////                okHttpClient = new OkHttpClient();
	//                okHttpClient = new OkHttpClient.Builder()
	//                        .connectTimeout(TIME_OUT, TimeUnit.SECONDS)
	//                        .writeTimeout(TIME_OUT, TimeUnit.SECONDS)
	//                        .readTimeout(TIME_OUT, TimeUnit.SECONDS)
	//                        .build();
	//            }
	//            return okHttpClient;
	//        }
	//    }


	override fun onRequest(url: String, httpContext: HttpContext, listener: RequestListener, handler: Handler) {
		//        AsyncHttpResponseHandler responseHandler = new AsyncHttpResponseHandler() {
		//
		//            @Override
		//            protected void handleFailureMessage(Throwable arg0, String arg1) {
		//                super.handleFailureMessage(arg0, arg1);
		//                httpContext.setResponse(arg1);
		//                LogUtils.i(HttpHelper.TAG, "response data:" + arg1);
		//                LogWatcher.getInstance().putRequestInfo("[response: " + url + "]\n" + arg1);
		//            }
		//
		//            @Override
		//            protected void handleSuccessMessage(int arg0, String responseJson) {
		//                super.handleSuccessMessage(arg0, responseJson);
		//                httpContext.setResponse(responseJson);
		//            }
		//
		//        };
		//        SyncHttpClient httpClient = HttpClientHelper.getSyncHttpClient();

		val options = httpContext.options
		//        if (url.toLowerCase(Locale.CHINA).startsWith("https")) {
		//            String cer = "";
		//            String cerPassWord = "";
		//            if (options != null) {
		//                cer = options.getHttpsCer();
		//                cerPassWord = options.getHttpsCerPassWord();
		//            }
		//            SSLSocketFactory socketFactory = HttpClientHelper.getSocketFactory(httpContext.getContext(), cer, cerPassWord);
		//            if (socketFactory != null) {
		//                httpClient.setSSLSocketFactory(socketFactory);
		//            }
		//        }
		//
		//        //设置header
		//        if (null != options && null != options.getHeader() && !options.getHeader().isEmpty()) {
		//            Iterator<Entry<String, String>> i = options.getHeader().entrySet().iterator();
		//            while (i.hasNext()) {
		//                Object o = i.next();
		//                String key = o.toString();
		//                String value = options.getHeader().get(key);
		//                httpClient.addHeader(key, value);
		//            }
		//        }
		//
		//        if (options != null && options.getTimeout() != 0) {
		//            httpClient.setTimeout(options.getTimeout());
		//        } else {
		//            httpClient.setTimeout(TIME_OUT);
		//        }

		// String request =
		// JsonParseUtils.obj2Json(httpContext.getRequestObject());
		// String request = jsonFormatter(request);
		// LogUtils.i(HttpHelper.TAG, "request data:" + request);
		// LogWatcher.getInstance().putRequestInfo("[request: " + url + "]\n" +
		// jsonFormatter(request));

		//        RequestParams params = new RequestParams();
		//        Map<String, Object> requestMap = getMapForJson(httpContext.getRequest());
		//        if (requestMap != null) {
		//            for (String key : requestMap.keySet()) {
		//                params.put(key, requestMap.get(key).toString());
		//            }
		//        }


		//        if (options != null && options.getRequestType().toLowerCase(Locale.CHINA).equals("get")) {
		//            httpClient.get(httpContext.getContext(), url + httpContext.getRequest(), responseHandler);
		//        } else {
		//            httpClient.post(httpContext.getContext(), url, params, responseHandler);
		//        }


		//        OkHttpClient okHttp = getOkHttpClient();

		//        Map<String, String> header = httpContext.getOptions().getHeader();
		//        if (null != header && !header.isEmpty()) {
		//            MultipartBody.Builder builder = new MultipartBody.Builder();
		//            builder.setType(MultipartBody.FORM);
		//
		//            for (String key : header.keySet()) {
		//                String value = header.get(key);
		////                "form-data; name=\"" + fileKeyName + "\"; filename=\"" + fileName + "\""
		////                RequestBody.create(MediaType.parse(guessMimeType(fileName)), file);
		////                RequestBody.create(null, params.get(key))
		//
		//                builder.addPart(Headers.of("Content-Disposition", key), RequestBody.create(null, v));
		//            }
		//
		//            builder.build();
		//        }


		try {
			val getUrl = url + if (null == httpContext.request) "" else httpContext.request
			val request: Request
			if (!TextUtils.isEmpty(options!!.requestType) && options.requestType!!.toLowerCase(Locale.CHINA).equals("get")) {
				LogUtils.i("HttpHelperDebug", "request url is[get]:$getUrl")
				request = Request.Builder().url(getUrl).get().build()
			} else {
				LogUtils.i("HttpHelperDebug", "request url is[post]:$url")
				//                RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), httpContext.getRequest());

				val formBody = FormBody.Builder()
						.add("json", httpContext.request)
						.build()
				request = Request.Builder().url(url).post(formBody).build()
				LogUtils.i("HttpHelperDebug", "request is:" + request.toString())
			}

			val okHttpClient: OkHttpClient
			if (0 != options.timeout) {
				okHttpClient = OkHttpClient.Builder()
						.connectTimeout(httpContext.options!!.timeout.toLong(), TimeUnit.SECONDS)
						.writeTimeout(httpContext.options!!.timeout.toLong(), TimeUnit.SECONDS)
						.readTimeout(httpContext.options!!.timeout.toLong(), TimeUnit.SECONDS)
						.build()
			} else {
				okHttpClient = OkHttpClientHelper.INSTANCE.getOkHttpClient()!!
			}


			val response = okHttpClient.newCall(request).execute()
			httpContext.httpCode = response.code()
			if (response.isSuccessful) {
				httpContext.response=response.body().string()
			} else {
				//                httpContext.code = response.code();
				//                httpContext.message = response.message();

				val serviceResponse: String
				if (response.body() != null) {
					serviceResponse = response.body().string()
				} else {
					serviceResponse = response.toString()
				}
				httpContext.response=serviceResponse
			}
		} catch (e: IOException) {
			e.printStackTrace()
		}

		httpContext.responseTimeStamp = System.currentTimeMillis()

		//
		// String response = null;
		// try {
		// LogWatcher.getInstance().putRequestInfo("1111");
		// response = post(url, httpContext.getRequest());
		// LogWatcher.getInstance().putRequestInfo("2222");
		// } catch (IOException e) {
		// LogWatcher.getInstance().putRequestInfo("3333" + e.getMessage());
		// e.printStackTrace();
		// } finally {
		// httpContext.setResponse(response);
		// LogUtils.i(HttpHelper.TAG, "response data:" + response);
		// LogWatcher.getInstance().putRequestInfo("[response: " + url + "]\n" +
		// response);
		// }
	}


	override fun onResponse(url: String, httpContext: HttpContext, listener: RequestListener, handler: Handler) {

	}

	companion object {

		/**
		 * Json 转成 Map<>
		 *
		 * @param jsonStr
		 * @return
		 */
		fun getMapForJson(jsonStr: String): HashMap<String, Any?>? {
			val jsonObject: JSONObject
			try {
				jsonObject = JSONObject(jsonStr)

				val keyIter = jsonObject.keys()
				var key: String
				var value: Any
				val valueMap = HashMap<String, Any?>()
				while (keyIter.hasNext()) {
					key = keyIter.next()
					value = jsonObject.get(key)
					// valueMap.put(key, getMapForJson(value.toString()));
					if (jsonObject.optJSONObject(key) != null) {
						valueMap[key] = getMapForJson(value.toString())
					} else {
						valueMap[key] = value
					}
				}
				return valueMap
			} catch (e: Exception) {
				e.printStackTrace()
			}

			return null
		}


		fun jsonFormatter(uglyJSONString: String): String {
			var prettyJsonStr2 = uglyJSONString
			try {
				val gson = GsonBuilder().setPrettyPrinting().create()
				val jp = JsonParser()
				val je = jp.parse(uglyJSONString)
				prettyJsonStr2 = gson.toJson(je)
			} catch (e: Exception) {
				e.printStackTrace()
			}

			return prettyJsonStr2
		}
	}

}
