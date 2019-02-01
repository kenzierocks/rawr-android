/**
 * Shortcuts for injection to increase code-mirroring between Activity and Fragment.
 */
package net.octyl.rawr.android.dagger

import android.app.Activity
import androidx.fragment.app.Fragment
import dagger.android.AndroidInjection
import dagger.android.support.AndroidSupportInjection

fun Activity.injectDependencies() = AndroidInjection.inject(this)
fun Fragment.injectDependencies() = AndroidSupportInjection.inject(this)