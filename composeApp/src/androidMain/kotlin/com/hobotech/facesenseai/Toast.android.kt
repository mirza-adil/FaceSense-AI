package com.hobotech.facesenseai

import android.widget.Toast

actual fun showVerificationSuccessToast(context: Any?) {
    (context as? android.content.Context)?.let { ctx ->
        Toast.makeText(ctx, "All steps verified!", Toast.LENGTH_SHORT).show()
    }
}
