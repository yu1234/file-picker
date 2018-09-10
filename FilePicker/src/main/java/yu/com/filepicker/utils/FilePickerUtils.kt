package yu.com.filepicker.utils

import com.blankj.utilcode.util.FileUtils
import yu.com.filepicker.R
import java.text.DecimalFormat

object FilePickerUtils {
    /**
     * 获取文件icon资源
     *
     * @param filename
     * @return
     */
    fun getFileIconRes(fileName: String): Int {
        val fileType =FileUtils.getFileExtension(fileName)
        if (fileType == null || fileType == "") {
            return R.mipmap.attachment_icon_other_m_default
        }
        if (fileType.indexOf("doc") != -1) {
            return R.mipmap.attachment_icon_word_m_default
        }
        if (fileType.indexOf("xls") != -1) {
            return R.mipmap.attachment_icon_excel_m_default
        }
        if (fileType.indexOf("ppt") != -1) {
            return R.mipmap.attachment_icon_ppt_m_default
        }
        if (fileType.indexOf("pdf") != -1) {
            return R.mipmap.attachment_icon_pdf_m_default
        }
        if (fileType.indexOf("txt") != -1) {
            return R.mipmap.attachment_icon_txt_m_default
        }
        if (fileType.indexOf("eml") != -1) {
            return R.mipmap.attachment_icon_eml_m_default
        }
        if (fileType.indexOf("jar") != -1) {
            return R.mipmap.attachment_icon_jar_m_default
        }
        val imgTypes = "jpg-jpeg-png-gif-bmp-pcx-tiff-tga-exif-fpx-svg-psd-cdr-pcd"
        if (imgTypes.indexOf(fileType) != -1) {
            return R.mipmap.attachment_icon_img_m_default
        }
        val videoType = "avi-wmv-mpeg-mp4-mov-mkv-flv-f4v-m4v-rmvb-rm-3gp-dat-ts-mts-vob"
        if (videoType.indexOf(fileType) != -1) {
            return R.mipmap.attachment_icon_video_m_default
        }
        val audioType = "cd-wave-aiff-mpeg-mp3-mpeg-4-midi-wma-realaudio-vqf-oggvorbis-amr-ape-flac-aac"
        if (audioType.indexOf(fileType) != -1) {
            return R.mipmap.attachment_icon_audio_m_default
        }
        val compressedType = "rar-zip-7z-tgz-tar-iso-cab-gz"
        return if (compressedType.indexOf(fileType) != -1) {
            R.mipmap.attachment_icon_compressed_m_default
        } else R.mipmap.attachment_icon_other_m_default
    }

    /**
     * 获取可读文件大小
     *
     * @param size
     * @return
     */
    fun getReadableByteSize(size: Long): String {
        val GB = (1024 * 1024 * 1024).toLong()//定义GB的计算常量
        val MB = (1024 * 1024).toLong()//定义MB的计算常量
        val KB: Long = 1024//定义KB的计算常量
        val df = DecimalFormat("0.00")//格式化小数
        var resultSize = ""
        if (size / GB >= 1) {
            //如果当前Byte的值大于等于1GB
            resultSize = df.format((size / GB.toFloat()).toDouble()) + "GB"
        } else if (size / MB >= 1) {
            //如果当前Byte的值大于等于1MB
            resultSize = df.format((size / MB.toFloat()).toDouble()) + "MB"
        } else if (size / KB >= 1) {
            //如果当前Byte的值大于等于1KB
            resultSize = df.format((size / KB.toFloat()).toDouble()) + "KB"
        } else {
            resultSize = size.toString() + "字节"
        }
        return resultSize
    }
}