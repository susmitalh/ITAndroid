package com.locatocam.app.views.settings

import android.widget.ImageView
import com.locatocam.app.data.responses.user_model.Document
import com.locatocam.app.views.settings.adapters.DocumentAdapter

data class DocLauncher(var document: Document, var documentAdapter: ImageView)
