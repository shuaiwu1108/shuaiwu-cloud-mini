package com.shuaiwu.cloud.framework.common.util.io;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import lombok.SneakyThrows;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

/**
 * 文件工具类
 *
 * @author 芋道源码
 */
public class FileUtils {

    /**
     * 创建临时文件
     * 该文件会在 JVM 退出时，进行删除
     *
     * @param data 文件内容
     * @return 文件
     */
    @SneakyThrows
    public static File createTempFile(String data) {
        File file = createTempFile();
        // 写入内容
        FileUtil.writeUtf8String(data, file);
        return file;
    }

    /**
     * 创建临时文件
     * 该文件会在 JVM 退出时，进行删除
     *
     * @param data 文件内容
     * @param suffix 文件后缀名，例如 ".txt"、".jpg" 等
     * @return 文件
     */
    @SneakyThrows
    public static File createTempFile(String data, String suffix) {
        File file = createTempFileWithSuffix(suffix);
        // 写入内容
        FileUtil.writeUtf8String(data, file);
        return file;
    }

    /**
     * 创建临时文件
     * 该文件会在 JVM 退出时，进行删除
     *
     * @param data 文件内容
     * @return 文件
     */
    @SneakyThrows
    public static File createTempFile(byte[] data) {
        File file = createTempFile();
        // 写入内容
        FileUtil.writeBytes(data, file);
        return file;
    }

    /**
     * 创建临时文件
     * 该文件会在 JVM 退出时，进行删除
     *
     * @param data 文件内容
     * @param suffix 文件后缀名，例如 ".txt"、".jpg" 等
     * @return 文件
     */
    @SneakyThrows
    public static File createTempFile(byte[] data, String suffix) {
        File file = createTempFileWithSuffix(suffix);
        // 写入内容
        FileUtil.writeBytes(data, file);
        return file;
    }

    /**
     * 创建临时文件，无内容
     * 该文件会在 JVM 退出时，进行删除
     *
     * @return 文件
     */
    @SneakyThrows
    public static File createTempFile() {
        // 创建文件，通过 UUID 保证唯一
        File file = File.createTempFile(IdUtil.simpleUUID(), null);
        // 标记 JVM 退出时，自动删除
        file.deleteOnExit();
        return file;
    }

    /**
     * 创建临时文件，无内容
     * 该文件会在 JVM 退出时，进行删除
     *
     * @param suffix 文件后缀名，例如 ".txt"、".jpg" 等
     * @return 文件
     */
    @SneakyThrows
    public static File createTempFileWithSuffix(String suffix) {
        // 创建文件，通过 UUID 保证唯一
        File file = File.createTempFile(IdUtil.simpleUUID(), suffix);
        // 标记 JVM 退出时，自动删除
        file.deleteOnExit();
        return file;
    }

    public static byte[] getFileContent(String url) {
        return HttpUtil.downloadBytes(url);
    }

    /**
     * 从URL自动解析文件名和类型，并转换为MultipartFile对象
     * @param imageUrl 图片URL
     * @return MultipartFile对象
     * @throws IOException 处理过程中的IO异常
     */
    public static MultipartFile convert(String imageUrl) throws IOException {
        // 下载图片字节数组，增加超时设置
        byte[] imageBytes = HttpUtil.createGet(imageUrl)
                .setConnectionTimeout(5000)
                .setReadTimeout(5000)
                .execute()
                .bodyBytes();

        // 解析文件名
        String fileName = parseFileNameFromUrl(imageUrl);

        // 解析文件类型（MIME类型）
        String contentType = parseContentTypeFromFileName(fileName);

        // 将字节数组转换为输入流
        return new CustomMultipartFile(
                imageBytes,
                "file",       // 参数名
                fileName,     // 原始文件名
                contentType   // 内容类型
        );
    }

    /**
     * 从URL中解析文件名
     */
    private static String parseFileNameFromUrl(String url) {
        try {
            // 解码URL，处理包含特殊字符的文件名
            String decodedUrl = URLDecoder.decode(url, StandardCharsets.UTF_8.name());

            // 提取URL中的路径部分
            URL urlObj = new URL(decodedUrl);
            String path = urlObj.getPath();

            // 从路径中提取文件名
            int lastSlashIndex = path.lastIndexOf('/');
            if (lastSlashIndex != -1 && lastSlashIndex < path.length() - 1) {
                String fileName = path.substring(lastSlashIndex + 1);
                // 如果文件名不为空则返回
                if (StrUtil.isNotBlank(fileName)) {
                    return fileName;
                }
            }

            // 如果无法从路径提取，使用默认文件名+时间戳
            return "image_" + System.currentTimeMillis() + ".jpg";
        } catch (Exception e) {
            // 发生异常时使用默认文件名
            return "image_" + System.currentTimeMillis() + ".jpg";
        }
    }

    /**
     * 根据文件名解析内容类型（MIME类型）
     * 不依赖Hutool的ContentType枚举，避免可能的找不到类问题
     */
    private static String parseContentTypeFromFileName(String fileName) {
        // 获取文件扩展名
        int lastDotIndex = fileName.lastIndexOf('.');
        if (lastDotIndex != -1 && lastDotIndex < fileName.length() - 1) {
            String extension = fileName.substring(lastDotIndex + 1).toLowerCase();

            // 直接定义MIME类型字符串，避免依赖特定枚举
            switch (extension) {
                case "jpg":
                case "jpeg":
                    return "image/jpeg";
                case "png":
                    return "image/png";
                case "gif":
                    return "image/gif";
                case "bmp":
                    return "image/bmp";
                case "webp":
                    return "image/webp";
                case "svg":
                    return "image/svg+xml";
                default:
                    // 未知图片类型，默认返回jpeg
                    return "image/jpeg";
            }
        }

        // 没有扩展名时默认返回jpeg类型
        return "image/jpeg";
    }

}
