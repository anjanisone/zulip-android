package com.humbughq.android;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

class AsyncPointerUpdate extends HumbugAsyncPushTask {

    boolean receivedPointer;

    public AsyncPointerUpdate(HumbugActivity humbugActivity) {
        super(humbugActivity);
    }

    public final void execute() {
        this.receivedPointer = true;
        execute("api/v1/get_profile");
    }

    public final void execute(int newPointer) {
        this.receivedPointer = false;
        this.setProperty("client_id", this.context.client_id);
        this.setProperty("pointer", newPointer + "");
        execute("api/v1/update_pointer");
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        if (this.receivedPointer) {
            try {
                final int pointer = (new JSONObject(result)).getInt("pointer");
                this.context.client_id = (new JSONObject(result))
                        .getString("client_id");
                Log.i("pointer", "got from server as " + pointer);

                final Message message = this.context.messageIndex.get(pointer);
                if (message == null) {
                    /*
                     * We're missing the pointer in the fetched message view!
                     * 
                     * This is totally okay, because if AsyncPointerUpdate is
                     * run at the start of the activity there will be no
                     * messages loaded at all. In any case, we now retrieve
                     * messages before and after the pointer.
                     */
                    Log.d("pointer", pointer + " not found in message list.");

                    this.context.current_poll = new AsyncPoller(this.context,
                            false, false);

                    this.context.current_poll
                            .setCallback(new AsyncTaskCompleteListener() {
                                @Override
                                public void onTaskComplete(String result) {

                                    context.listView.setSelection(context.adapter
                                            .getPosition(context.messageIndex
                                                    .get(pointer)));
                                    context.listView.scrollBy(0, 500);
                                    context.current_poll = new AsyncPoller(
                                            context, true, false);
                                    context.current_poll.execute(
                                            (int) context.adapter
                                                    .getItemId(context.adapter
                                                            .getCount() - 1),
                                            "newer", 10000);
                                }

                            });
                    this.context.current_poll.execute(pointer, "around", 200);

                } else {
                    this.context.listView.setSelection(this.context.adapter
                            .getPosition(message));
                }

                callback.onTaskComplete(result);
            } catch (JSONException e) {
                Log.e("json", "parsing error");
                e.printStackTrace();
            }
        }
    }
}