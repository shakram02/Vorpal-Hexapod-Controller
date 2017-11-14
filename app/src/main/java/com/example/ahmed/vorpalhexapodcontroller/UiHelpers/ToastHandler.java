package com.example.ahmed.vorpalhexapodcontroller.UiHelpers;

import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

/**
 * A class for showing a <code>Toast</code> from background processes using a
 * <code>Handler</code>.
 *
 * @author kaolick
 */
public class ToastHandler {
    // General attributes
    private Context Context;
    private Handler Handler;

    /**
     * Class constructor.
     *
     * @param context The <code>Context</code> for showing the <code>Toast</code>
     */
    public ToastHandler(Context context) {
        this.Context = context;
        this.Handler = new Handler();
    }

    /**
     * Runs the <code>Runnable</code> in a separate <code>Thread</code>.
     *
     * @param runnable The <code>Runnable</code> containing the <code>Toast</code>
     */
    private void run(final Runnable runnable) {
        Thread thread = new Thread() {
            public void run() {
                Handler.post(runnable);
            }
        };

        thread.start();
    }

    /**
     * Shows a <code>Toast</code> using a <code>Handler</code>. Can be used in
     * background processes.
     *
     * @param resId    The resource id of the string resource to use. Can be
     *                 formatted text.
     * @param duration How long to display the message. Only use LENGTH_LONG or
     *                 LENGTH_SHORT from <code>Toast</code>.
     */
    public void showToast(final int resId, final int duration) {
        final Runnable runnable = () -> {
            // Get the text for the given resource ID
            String text = Context.getResources().getString(resId);

            Toast.makeText(Context, text, duration).show();
        };

        run(runnable);
    }

    /**
     * Shows a <code>Toast</code> using a <code>Handler</code>. Can be used in
     * background processes.
     *
     * @param text     The text to show. Can be formatted text.
     * @param duration How long to display the message. Only use LENGTH_LONG or
     *                 LENGTH_SHORT from <code>Toast</code>.
     */
    public void showToast(final CharSequence text, final int duration) {
        final Runnable runnable = () -> Toast.makeText(Context, text, duration).show();
        run(runnable);
    }

    public void threadErrorToast(final Thread thread, final Throwable exc) {
        Log.e(getClass().getSimpleName(), String.format("Thread {%s} threw an exception: %s", thread.toString(), exc));
        this.showToast(exc.getMessage(), Toast.LENGTH_LONG);
    }
}