package com.hobotech.facesenseai

/**
 * Shows a short "verification success" message using platform Toast (Android) or equivalent (iOS).
 * @param context Platform context; on Android use Activity or Application context, on iOS can be null.
 */
expect fun showVerificationSuccessToast(context: Any?)
