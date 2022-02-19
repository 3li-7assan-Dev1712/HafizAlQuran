package app.netlify.dev_ali_hassan.hafizalquran.util

import android.app.Activity
import android.content.Context
import android.content.res.AssetManager
import android.system.Os.open

object MediaUtil {

    fun createTestFile(context: Context) {
        val fileName = "ali_file"
        val fileContent = "Hello World!"
        context.getDir("my_directory", Context.MODE_PRIVATE)
        context.openFileOutput(fileName, Context.MODE_PRIVATE).use {
            it.write(fileContent.toByteArray())

        }


//        val bytes = AssetManager.open("ali.mp3", 0, Activity.MODE_PRIVATE).readBytes()


    }

    fun getFileList(context: Context): Array<String> =
        context.fileList()

}