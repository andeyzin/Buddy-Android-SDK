
package test.com.buddy.sdk;


import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.AsyncTask;
import android.test.InstrumentationTestCase;
import android.util.Log;

import com.buddy.sdk.Buddy;
import com.buddy.sdk.BuddyCallback;
import com.buddy.sdk.BuddyClient;
import com.buddy.sdk.BuddyClientOptions;
import com.buddy.sdk.models.Checkin;
import com.buddy.sdk.models.PagedResult;
import com.buddy.sdk.models.Picture;
import com.buddy.sdk.models.User;
import com.buddy.sdk.BuddyFile;
import com.buddy.sdk.BuddyResult;
import com.google.gson.JsonObject;

import junit.framework.Assert;

import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class BasicTest extends InstrumentationTestCase {

    private static final String TargetUrl = null;
    private static final String AppId = "your_appid";
    private static final String AppKey = "your_appkey";

    private BuddyClient getClient() {
        return getClient(AppId, AppKey, true);
    }

    private BuddyClient getClient(String appid, String appkey, boolean syncMode) {

        if (appid != null && appid.startsWith("your")) {

            Assert.fail("Please specify an appid and appkey in the AppId and AppKey fields of test.com.buddy.sdk.BasicTest to run tests.");

        }

        BuddyClientOptions options = new BuddyClientOptions();
        options.synchronousMode = syncMode;
        options.serviceRoot = TargetUrl;

        BuddyClient client = Buddy.init(null, appid == null ? "bbbbbc.fakevlNmjKbj" : appid, appkey == null ? "BADBAD15-D1DA-4DD2-BA8B-566B9F33385E" : appkey, options);

        return client;
    }

    public void testPingSuccess() throws Exception {
        BuddyClient client = getClient();

        Future<BuddyResult<String>> handle = client.get("/service/ping", null, String.class);

        BuddyResult<String> result = handle.get();

        assertNotNull(result);
        assertNull(result.getError());
        assertEquals("Pong", result.getResult());
    }

    public void testPingFail() throws Exception {

        BuddyClient client = getClient(null, null, true);

        Future<BuddyResult<String>> handle = client.get("/service/ping", null, String.class);

        BuddyResult<String> result = handle.get();
        assertNotNull(result);
        assertEquals("AuthAppCredentialsInvalid", result.getError());
        assertEquals(null, result.getResult());

    }


    public void testCreateUser() throws Exception {
        BuddyClient client = getClient();

        String newUser = String.format(Locale.getDefault(), "%s-%d", "testuser-", new Date().getTime());

        Future<BuddyResult<User>> handle = client.createUser(newUser, "password", null, null, null, null, null, null, null);

        BuddyResult<User> result = handle.get();
        assertNotNull(result);
        assertNull(result.getError());
        assertEquals(newUser, result.getResult().userName);

        Future<BuddyResult<User>> handle2 = client.getCurrentUser(null);

        result = handle2.get();
        assertEquals(newUser, result.getResult().userName);

    }

    public void testLoginUser() throws Exception {

        BuddyClient client = getClient();

        Future<BuddyResult<User>> handle = client.loginUser("shawn", "password", null);

        BuddyResult<User> result = handle.get();
        assertNotNull(result);
        assertNull(result.getError());
        assertEquals("shawn", result.getResult().userName);


    }

    public void testPatchDevice() throws Exception {

        BuddyClient client = getClient();

        Future<BuddyResult<Boolean>> handle = client.setPushToken("BPA91bF0Th8nRgxfhdENtWLyFWmAh9jZ3DZzIUtXvb7Z2yXpH-7B2H59BlDNy7ZigxcJS1V5rezbUFAyZreIQWaQz3MfJ61CmfDwK-cH9-1DaOQl3Kx0iptGWjZr1e5AxbYFMeHzFjI-kGCr6nrLUNeCEFkNXgnX101p0v-TmKDGguN6JXqWMAc", null);
        BuddyResult<Boolean> result = handle.get();

        assertNotNull(result);
        assertNull(result.getError());
        assertTrue(result.getResult());

    }

    public void testPostCheckin() throws Exception {

        final BuddyClient client = getClient();

        Map<String,Object> params = new HashMap<String, Object>();
        params.put("comment", "this is a test");
        Location loc = new Location("BuddyTest");
        loc.setLatitude(47);
        loc.setLongitude(-122);
        client.setLastLocation(loc);

        client.<Checkin>post("/checkins", params, new BuddyCallback<Checkin>(Checkin.class) {
            @Override
            public void completed(BuddyResult<Checkin> result) {

                client.setLastLocation(null);

                Checkin r = result.getResult();
                assertNotNull(r.location);
                assertEquals((int)47.0, (int)r.location.getLatitude());
            }
        });



    }

    public void testDelete() throws Exception {


        BuddyClient client = getClient();

        Future<BuddyResult<JsonObject>> handle = client.delete("/metrics/events/abc.123", null, JsonObject.class);
        BuddyResult<JsonObject> result = handle.get();

        assertNotNull(result);
        assertEquals("ParameterIncorrectFormat", result.getError());
    }



    private class MyRoundCornerDrawable extends Drawable {

        private Paint paint;

        public MyRoundCornerDrawable(Bitmap bitmap) {
            BitmapShader shader;
            shader = new BitmapShader(bitmap, Shader.TileMode.CLAMP,
                    Shader.TileMode.CLAMP);
            paint = new Paint();
            paint.setAntiAlias(true);
            paint.setShader(shader);
        }

        @Override
        public void draw(Canvas canvas) {
            int height = getBounds().height();
            int width = getBounds().width();
            RectF rect = new RectF(0.0f, 0.0f, width, height);
            canvas.drawRoundRect(rect, 30, 30, paint);
        }

        @Override
        public void setAlpha(int alpha) {
            paint.setAlpha(alpha);
        }

        @Override
        public void setColorFilter(ColorFilter cf) {
            paint.setColorFilter(cf);
        }

        @Override
        public int getOpacity() {
            return PixelFormat.TRANSLUCENT;
        }

    }

    public void testRunInThread() throws Exception {

        final BuddyClient client = getClient(AppId, AppKey, false);

        AsyncTask<Void,Void, Boolean> task = new AsyncTask<Void, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Void... params) {
                Future promise = client.getCurrentUser(null);
                try {
                    promise.get();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
                return true;
            }
        };

        task.execute();

    }


    public void testUploadFile() throws Exception{

        BuddyClient client = getClient();

        Future handle = client.loginUser("shawn", "password", null);

        handle.get();

        Map<String, Object> parameters = new HashMap<String,Object>();


        // generate a PNG for upload...
        Bitmap bitmap = Bitmap.createBitmap(30, 30, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        MyRoundCornerDrawable drawable = new MyRoundCornerDrawable(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] bytes = stream.toByteArray();
        InputStream is = new ByteArrayInputStream(bytes);

        parameters.put("caption", "From Android");
        parameters.put("data", new BuddyFile(is, "image/png"));
        parameters.put("title","The Title");

        Future<BuddyResult<Picture>> handle2 = client.<Picture>post("/pictures", parameters, Picture.class);

        handle2.get();

        Picture picture = handle2.get().getResult();
        assertNotNull(handle2.get().getResult());
        assertEquals("From Android", picture.caption);
        assertEquals(30,picture.size.h);
        assertEquals(30,picture.size.w);
        assertEquals("The Title",picture.title);

        // now get the file.
        //
        parameters.clear();
        Future<BuddyResult<BuddyFile>> handle3 = client.<BuddyFile>get(String.format("/pictures/%s/file", handle2.get().getResult().id), null, BuddyFile.class);

        handle3.get();

        assertNotNull(handle3.get());

        BuddyFile file = handle3.get().getResult();

        assertNotNull(file);
        assertNotNull(file.getStream());
        assertEquals("image/png", file.getContentType());
        InputStream fileStream = file.getStream();

        assertEquals(bytes.length, fileStream.available());

        byte[] buffer = new byte[fileStream.available()];
        fileStream.read(buffer, 0, buffer.length);

        for (int i= 0; i < buffer.length; i++) {
            if (buffer[i] != bytes[i]) {
                Assert.fail("Bytes not equal at " + i);
            }

        }

    }

    public void testSearch() throws Exception {

        BuddyClient client = getClient();

        Future handle = client.loginUser("shawn", "password", null);

        handle.get();

        Map<String, Object> parameters = new HashMap<String,Object>();
        parameters.put("description","description1");
        parameters.put("comment","my first comment");
        Location location = new Location("Buddy");
        location.setLatitude(11.2);
        location.setLongitude(33.4);
        parameters.put("location",location);
        Future<BuddyResult<Checkin>> handle2 = client.<Checkin>post("/checkins", parameters, Checkin.class);

        handle2.get();

        Checkin checkin1 = handle2.get().getResult();
        assertNotNull(handle2.get().getResult());
        assertEquals("description1", checkin1.description);
        assertEquals("my first comment",checkin1.comment);
        assertEquals(11.2,checkin1.location.getLatitude());
        assertEquals(33.4,checkin1.location.getLongitude());

        parameters.put("comment","my second comment");
        Future<BuddyResult<Checkin>> handle3 = client.<Checkin>post("/checkins", parameters, Checkin.class);

        parameters.put("description","dont search me");
        parameters.put("comment","my third comment");
        Future<BuddyResult<Checkin>> handle4 = client.<Checkin>post("/checkins", parameters, Checkin.class);

        Map<String, Object> searchParameters = new HashMap<String,Object>();
        searchParameters.put("description","description1");
        Future<BuddyResult<PagedResult>> handle5 = client.<PagedResult>get("/checkins",searchParameters,PagedResult.class);

        PagedResult searchResults = handle5.get().getResult();
        assertTrue(searchResults.pageResults.size()>0);
        List<Checkin> checkins = searchResults.convertPageResults(Checkin.class);

        for(Checkin checkin : checkins) {
            assertTrue(checkin.description.startsWith("description1"));
        }
    }
}