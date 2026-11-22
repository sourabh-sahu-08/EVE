package com.eve.assistant.ui.components

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.path
import androidx.compose.ui.unit.dp

/**
 * Built-in vector icons for EVE eliminating external icon dependencies.
 */
object EveIcons {

    val Mic: ImageVector by lazy {
        ImageVector.Builder(
            name = "Mic",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(fill = SolidColor(Color.White)) {
                moveTo(12f, 14f)
                curveTo(13.66f, 14f, 15f, 12.66f, 15f, 11f)
                lineTo(15f, 5f)
                curveTo(15f, 3.34f, 13.66f, 2f, 12f, 2f)
                curveTo(10.34f, 2f, 9f, 3.34f, 9f, 5f)
                lineTo(9f, 11f)
                curveTo(9f, 12.66f, 10.34f, 14f, 12f, 14f)
                close()
                moveTo(17f, 11f)
                curveTo(17f, 13.76f, 14.76f, 16f, 12f, 16f)
                curveTo(9.24f, 16f, 7f, 13.76f, 7f, 11f)
                lineTo(5f, 11f)
                curveTo(5f, 14.53f, 7.61f, 17.44f, 11f, 17.93f)
                lineTo(11f, 21f)
                lineTo(13f, 21f)
                lineTo(13f, 17.93f)
                curveTo(16.39f, 17.44f, 19f, 14.53f, 19f, 11f)
                lineTo(17f, 11f)
                close()
            }
        }.build()
    }

    val Stop: ImageVector by lazy {
        ImageVector.Builder(
            name = "Stop",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(fill = SolidColor(Color.White)) {
                moveTo(6f, 6f)
                lineTo(18f, 6f)
                lineTo(18f, 18f)
                lineTo(6f, 18f)
                close()
            }
        }.build()
    }

    val Settings: ImageVector by lazy {
        ImageVector.Builder(
            name = "Settings",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(fill = SolidColor(Color.White)) {
                moveTo(19.14f, 12.94f)
                curveTo(19.18f, 12.63f, 19.2f, 12.32f, 19.2f, 12f)
                curveTo(19.2f, 11.68f, 19.18f, 11.37f, 19.14f, 11.06f)
                lineTo(21.27f, 9.4f)
                curveTo(21.46f, 9.25f, 21.51f, 8.98f, 21.39f, 8.76f)
                lineTo(19.39f, 5.3f)
                curveTo(19.27f, 5.08f, 19f, 5f, 18.78f, 5.09f)
                lineTo(16.27f, 6.1f)
                curveTo(15.75f, 5.7f, 15.19f, 5.37f, 14.58f, 5.12f)
                lineTo(14.2f, 2.45f)
                curveTo(14.16f, 2.2f, 13.95f, 2.02f, 13.7f, 2.02f)
                lineTo(9.7f, 2.02f)
                curveTo(9.45f, 2.02f, 9.24f, 2.2f, 9.2f, 2.45f)
                lineTo(8.82f, 5.12f)
                curveTo(8.21f, 5.37f, 7.65f, 5.7f, 7.13f, 6.1f)
                lineTo(4.62f, 5.09f)
                curveTo(4.4f, 5f, 4.13f, 5.08f, 4.01f, 5.3f)
                lineTo(2.01f, 8.76f)
                curveTo(1.89f, 8.98f, 1.94f, 9.25f, 2.13f, 9.4f)
                lineTo(4.26f, 11.06f)
                curveTo(4.22f, 11.37f, 4.2f, 11.68f, 4.2f, 12f)
                curveTo(4.2f, 12.32f, 4.22f, 12.63f, 4.26f, 12.94f)
                lineTo(2.13f, 14.6f)
                curveTo(1.94f, 14.75f, 1.89f, 15.02f, 2.01f, 15.24f)
                lineTo(4.01f, 18.7f)
                curveTo(4.13f, 18.92f, 4.4f, 19.01f, 4.62f, 18.92f)
                lineTo(7.13f, 17.91f)
                curveTo(7.65f, 18.31f, 8.21f, 18.64f, 8.82f, 18.89f)
                lineTo(9.2f, 21.56f)
                curveTo(9.24f, 21.8f, 9.45f, 21.98f, 9.7f, 21.98f)
                lineTo(13.7f, 21.98f)
                curveTo(13.95f, 21.98f, 14.16f, 21.8f, 14.2f, 21.56f)
                lineTo(14.58f, 18.89f)
                curveTo(15.19f, 18.64f, 15.75f, 18.31f, 16.27f, 17.91f)
                lineTo(18.78f, 18.92f)
                curveTo(19f, 19.01f, 19.27f, 18.92f, 19.39f, 18.7f)
                lineTo(21.39f, 15.24f)
                curveTo(21.51f, 15.02f, 21.46f, 14.75f, 21.27f, 14.6f)
                lineTo(19.14f, 12.94f)
                close()
                moveTo(12f, 15.5f)
                curveTo(10.07f, 15.5f, 8.5f, 13.93f, 8.5f, 12f)
                curveTo(8.5f, 10.07f, 10.07f, 8.5f, 12f, 8.5f)
                curveTo(13.93f, 8.5f, 15.5f, 10.07f, 15.5f, 12f)
                curveTo(15.5f, 13.93f, 13.93f, 15.5f, 12f, 15.5f)
                close()
            }
        }.build()
    }

    val History: ImageVector by lazy {
        ImageVector.Builder(
            name = "History",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(fill = SolidColor(Color.White)) {
                moveTo(13f, 3f)
                curveTo(8.03f, 3f, 4f, 7.03f, 4f, 12f)
                lineTo(1f, 12f)
                lineTo(4.89f, 15.89f)
                lineTo(4.96f, 16.03f)
                lineTo(9f, 12f)
                lineTo(6f, 12f)
                curveTo(6f, 8.13f, 9.13f, 5f, 13f, 5f)
                curveTo(16.87f, 5f, 20f, 8.13f, 20f, 12f)
                curveTo(20f, 15.87f, 16.87f, 19f, 13f, 19f)
                curveTo(11.07f, 19f, 9.32f, 18.21f, 8.06f, 16.94f)
                lineTo(6.64f, 18.36f)
                curveTo(8.27f, 19.99f, 10.51f, 21f, 13f, 21f)
                curveTo(17.97f, 21f, 22f, 16.97f, 22f, 12f)
                curveTo(22f, 7.03f, 17.97f, 3f, 13f, 3f)
                close()
                moveTo(12f, 7f)
                lineTo(12f, 13f)
                lineTo(16.28f, 15.54f)
                lineTo(17f, 14.33f)
                lineTo(13.5f, 12.25f)
                lineTo(13.5f, 7f)
                lineTo(12f, 7f)
                close()
            }
        }.build()
    }

    val ArrowBack: ImageVector by lazy {
        ImageVector.Builder(
            name = "ArrowBack",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(fill = SolidColor(Color.White)) {
                moveTo(20f, 11f)
                lineTo(7.83f, 11f)
                lineTo(13.42f, 5.41f)
                lineTo(12f, 4f)
                lineTo(4f, 12f)
                lineTo(12f, 20f)
                lineTo(13.41f, 18.59f)
                lineTo(7.83f, 13f)
                lineTo(20f, 13f)
                close()
            }
        }.build()
    }

    val ChevronRight: ImageVector by lazy {
        ImageVector.Builder(
            name = "ChevronRight",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(fill = SolidColor(Color.White)) {
                moveTo(10f, 6f)
                lineTo(8.59f, 7.41f)
                lineTo(13.17f, 12f)
                lineTo(8.59f, 16.59f)
                lineTo(10f, 18f)
                lineTo(16f, 12f)
                close()
            }
        }.build()
    }

    val Search: ImageVector by lazy {
        ImageVector.Builder(
            name = "Search",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(fill = SolidColor(Color.White)) {
                moveTo(15.5f, 14f)
                lineTo(14.71f, 14f)
                lineTo(14.43f, 13.73f)
                curveTo(15.41f, 12.59f, 16f, 11.11f, 16f, 9.5f)
                curveTo(16f, 5.91f, 13.09f, 3f, 9.5f, 3f)
                curveTo(5.91f, 3f, 3f, 5.91f, 3f, 9.5f)
                curveTo(3f, 13.09f, 5.91f, 16f, 9.5f, 16f)
                curveTo(11.11f, 16f, 12.59f, 15.41f, 13.73f, 14.43f)
                lineTo(14f, 14.71f)
                lineTo(14f, 15.5f)
                lineTo(19f, 20.49f)
                lineTo(20.49f, 19f)
                lineTo(15.5f, 14f)
                close()
                moveTo(9.5f, 14f)
                curveTo(7.01f, 14f, 5f, 11.99f, 5f, 9.5f)
                curveTo(5f, 7.01f, 7.01f, 5f, 9.5f, 5f)
                curveTo(11.99f, 5f, 14f, 7.01f, 14f, 9.5f)
                curveTo(14f, 11.99f, 11.99f, 14f, 9.5f, 14f)
                close()
            }
        }.build()
    }

    val Star: ImageVector by lazy {
        ImageVector.Builder(
            name = "Star",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(fill = SolidColor(Color.White)) {
                moveTo(12f, 17.27f)
                lineTo(18.18f, 21f)
                lineTo(16.54f, 13.97f)
                lineTo(22f, 9.24f)
                lineTo(14.81f, 8.63f)
                lineTo(12f, 2f)
                lineTo(9.19f, 8.63f)
                lineTo(2f, 9.24f)
                lineTo(7.46f, 13.97f)
                lineTo(5.82f, 21f)
                close()
            }
        }.build()
    }

    val StarBorder: ImageVector by lazy {
        ImageVector.Builder(
            name = "StarBorder",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(fill = SolidColor(Color.White)) {
                moveTo(22f, 9.24f)
                lineTo(14.81f, 8.62f)
                lineTo(12f, 2f)
                lineTo(9.19f, 8.63f)
                lineTo(2f, 9.24f)
                lineTo(7.46f, 13.97f)
                lineTo(5.82f, 21f)
                lineTo(12f, 17.27f)
                lineTo(18.18f, 21f)
                lineTo(16.54f, 13.97f)
                lineTo(22f, 9.24f)
                close()
                moveTo(12f, 15.4f)
                lineTo(8.24f, 17.67f)
                lineTo(9.24f, 13.39f)
                lineTo(5.92f, 10.51f)
                lineTo(10.3f, 10.13f)
                lineTo(12f, 6.1f)
                lineTo(13.71f, 10.14f)
                lineTo(18.09f, 10.52f)
                lineTo(14.77f, 13.4f)
                lineTo(15.77f, 17.68f)
                lineTo(12f, 15.4f)
                close()
            }
        }.build()
    }

    val DeleteSweep: ImageVector by lazy {
        ImageVector.Builder(
            name = "DeleteSweep",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(fill = SolidColor(Color.White)) {
                moveTo(15f, 16f)
                lineTo(19f, 16f)
                lineTo(19f, 18f)
                lineTo(15f, 18f)
                close()
                moveTo(15f, 8f)
                lineTo(21f, 8f)
                lineTo(21f, 10f)
                lineTo(15f, 10f)
                close()
                moveTo(15f, 12f)
                lineTo(20f, 12f)
                lineTo(20f, 14f)
                lineTo(15f, 14f)
                close()
                moveTo(3f, 18f)
                curveTo(3f, 19.1f, 3.9f, 20f, 5f, 20f)
                lineTo(11f, 20f)
                curveTo(12.1f, 20f, 13f, 19.1f, 13f, 18f)
                lineTo(13f, 8f)
                lineTo(3f, 8f)
                lineTo(3f, 18f)
                close()
                moveTo(13f, 5f)
                lineTo(10.5f, 5f)
                lineTo(9.79f, 4.29f)
                curveTo(9.61f, 4.11f, 9.35f, 4f, 9.09f, 4f)
                lineTo(6.91f, 4f)
                curveTo(6.65f, 4f, 6.39f, 4.11f, 6.21f, 4.29f)
                lineTo(5.5f, 5f)
                lineTo(3f, 5f)
                lineTo(3f, 7f)
                lineTo(13f, 7f)
                lineTo(13f, 5f)
                close()
            }
        }.build()
    }

    val ErrorOutline: ImageVector by lazy {
        ImageVector.Builder(
            name = "ErrorOutline",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(fill = SolidColor(Color.White)) {
                moveTo(11f, 15f)
                lineTo(13f, 15f)
                lineTo(13f, 17f)
                lineTo(11f, 17f)
                close()
                moveTo(11f, 7f)
                lineTo(13f, 7f)
                lineTo(13f, 13f)
                lineTo(11f, 13f)
                close()
                moveTo(11.99f, 2f)
                curveTo(6.47f, 2f, 2f, 6.48f, 2f, 12f)
                curveTo(2f, 17.52f, 6.47f, 22f, 11.99f, 22f)
                curveTo(17.52f, 22f, 22f, 17.52f, 22f, 12f)
                curveTo(22f, 6.48f, 17.52f, 2f, 11.99f, 2f)
                close()
                moveTo(12f, 20f)
                curveTo(7.58f, 20f, 4f, 16.42f, 4f, 12f)
                curveTo(4f, 7.58f, 7.58f, 4f, 12f, 4f)
                curveTo(16.42f, 4f, 20f, 7.58f, 20f, 12f)
                curveTo(20f, 16.42f, 16.42f, 20f, 12f, 20f)
                close()
            }
        }.build()
    }

    val Refresh: ImageVector by lazy {
        ImageVector.Builder(
            name = "Refresh",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(fill = SolidColor(Color.White)) {
                moveTo(17.65f, 6.35f)
                curveTo(16.2f, 4.9f, 14.21f, 4f, 12f, 4f)
                curveTo(7.58f, 4f, 4.01f, 7.58f, 4.01f, 12f)
                curveTo(4.01f, 16.42f, 7.58f, 20f, 12f, 20f)
                curveTo(15.73f, 20f, 18.84f, 17.45f, 19.73f, 14f)
                lineTo(17.65f, 14f)
                curveTo(16.83f, 16.33f, 14.61f, 18f, 12f, 18f)
                curveTo(8.69f, 18f, 6f, 15.31f, 6f, 12f)
                curveTo(6f, 8.69f, 8.69f, 6f, 12f, 6f)
                curveTo(13.66f, 6f, 15.14f, 6.69f, 16.22f, 7.78f)
                lineTo(13f, 11f)
                lineTo(20f, 11f)
                lineTo(20f, 4f)
                lineTo(17.65f, 6.35f)
                close()
            }
        }.build()
    }

    val Check: ImageVector by lazy {
        ImageVector.Builder(
            name = "Check",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(fill = SolidColor(Color.White)) {
                moveTo(9f, 16.17f)
                lineTo(4.83f, 12f)
                lineTo(3.41f, 13.41f)
                lineTo(9f, 19f)
                lineTo(21f, 7f)
                lineTo(19.59f, 5.59f)
                close()
            }
        }.build()
    }

    val CheckCircle: ImageVector by lazy {
        ImageVector.Builder(
            name = "CheckCircle",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(fill = SolidColor(Color.White)) {
                moveTo(12f, 2f)
                curveTo(6.48f, 2f, 2f, 6.48f, 2f, 12f)
                curveTo(2f, 17.52f, 6.48f, 22f, 12f, 22f)
                curveTo(17.52f, 22f, 22f, 17.52f, 22f, 12f)
                curveTo(22f, 6.48f, 17.52f, 2f, 12f, 2f)
                close()
                moveTo(10f, 17f)
                lineTo(5f, 12f)
                lineTo(6.41f, 10.59f)
                lineTo(10f, 14.17f)
                lineTo(17.59f, 6.58f)
                lineTo(19f, 8f)
                lineTo(10f, 17f)
                close()
            }
        }.build()
    }

    val Speed: ImageVector by lazy {
        ImageVector.Builder(
            name = "Speed",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(fill = SolidColor(Color.White)) {
                moveTo(20.38f, 8.57f)
                lineTo(18.96f, 10f)
                curveTo(19.62f, 11.21f, 20f, 12.57f, 20f, 14f)
                curveTo(20f, 18.41f, 16.41f, 22f, 12f, 22f)
                curveTo(7.59f, 22f, 4f, 18.41f, 4f, 14f)
                curveTo(4f, 9.59f, 7.59f, 6f, 12f, 6f)
                curveTo(13.43f, 6f, 14.79f, 6.38f, 16f, 7.04f)
                lineTo(17.43f, 5.62f)
                curveTo(15.84f, 4.6f, 13.98f, 4f, 12f, 4f)
                curveTo(6.48f, 4f, 2f, 8.48f, 2f, 14f)
                curveTo(2f, 19.52f, 6.48f, 24f, 12f, 24f)
                curveTo(17.52f, 24f, 22f, 19.52f, 22f, 14f)
                curveTo(22f, 12.02f, 21.4f, 10.16f, 20.38f, 8.57f)
                close()
                moveTo(10.59f, 15.41f)
                curveTo(10.22f, 15.04f, 10f, 14.54f, 10f, 14f)
                curveTo(10f, 12.9f, 10.9f, 12f, 12f, 12f)
                curveTo(12.54f, 12f, 13.04f, 12.22f, 13.41f, 12.59f)
                lineTo(17f, 9f)
                lineTo(10.59f, 15.41f)
                close()
            }
        }.build()
    }

    val VolumeUp: ImageVector by lazy {
        ImageVector.Builder(
            name = "VolumeUp",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(fill = SolidColor(Color.White)) {
                moveTo(3f, 9f)
                lineTo(3f, 15f)
                lineTo(7f, 15f)
                lineTo(12f, 20f)
                lineTo(12f, 4f)
                lineTo(7f, 9f)
                lineTo(3f, 9f)
                close()
                moveTo(16.5f, 12f)
                curveTo(16.5f, 10.23f, 15.48f, 8.71f, 14f, 7.97f)
                lineTo(14f, 16.02f)
                curveTo(15.48f, 15.29f, 16.5f, 13.77f, 16.5f, 12f)
                close()
                moveTo(14f, 3.23f)
                lineTo(14f, 5.29f)
                curveTo(16.89f, 6.15f, 19f, 8.83f, 19f, 12f)
                curveTo(19f, 15.17f, 16.89f, 17.85f, 14f, 18.71f)
                lineTo(14f, 20.77f)
                curveTo(18.01f, 19.86f, 21f, 16.28f, 21f, 12f)
                curveTo(21f, 7.72f, 18.01f, 4.14f, 14f, 3.23f)
                close()
            }
        }.build()
    }

    val Hearing: ImageVector by lazy {
        ImageVector.Builder(
            name = "Hearing",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(fill = SolidColor(Color.White)) {
                moveTo(17f, 20f)
                curveTo(14.88f, 20f, 13.14f, 18.36f, 13.01f, 16.27f)
                lineTo(11f, 16.27f)
                curveTo(11.13f, 19.46f, 13.77f, 22f, 17f, 22f)
                curveTo(20.31f, 22f, 23f, 19.31f, 23f, 16f)
                curveTo(23f, 13.29f, 21.2f, 10.99f, 18.66f, 10.27f)
                curveTo(18.23f, 6.74f, 15.23f, 4f, 11.5f, 4f)
                curveTo(7.36f, 4f, 4f, 7.36f, 4f, 11.5f)
                curveTo(4f, 13.57f, 4.84f, 15.45f, 6.19f, 16.81f)
                lineTo(7.61f, 15.39f)
                curveTo(6.62f, 14.4f, 6f, 13.03f, 6f, 11.5f)
                curveTo(6f, 8.46f, 8.46f, 6f, 11.5f, 6f)
                curveTo(14.22f, 6f, 16.48f, 7.97f, 16.92f, 10.57f)
                lineTo(16.5f, 10.57f)
                curveTo(13.46f, 10.57f, 11f, 13.03f, 11f, 16.07f)
                curveTo(11f, 18.28f, 12.79f, 20.07f, 15f, 20.07f)
                lineTo(17f, 20f)
                close()
            }
        }.build()
    }

    val RecordVoiceOver: ImageVector by lazy {
        ImageVector.Builder(
            name = "RecordVoiceOver",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(fill = SolidColor(Color.White)) {
                moveTo(9f, 13f)
                curveTo(11.21f, 13f, 13f, 11.21f, 13f, 9f)
                curveTo(13f, 6.79f, 11.21f, 5f, 9f, 5f)
                curveTo(6.79f, 5f, 5f, 6.79f, 5f, 9f)
                curveTo(5f, 11.21f, 6.79f, 13f, 9f, 13f)
                close()
                moveTo(9f, 15f)
                curveTo(6.33f, 15f, 1f, 16.34f, 1f, 19f)
                lineTo(1f, 21f)
                lineTo(17f, 21f)
                lineTo(17f, 19f)
                curveTo(17f, 16.34f, 11.67f, 15f, 9f, 15f)
                close()
                moveTo(16.76f, 5.36f)
                lineTo(15.34f, 6.78f)
                curveTo(16.37f, 7.81f, 17f, 9.22f, 17f, 10.77f)
                curveTo(17f, 12.32f, 16.37f, 13.73f, 15.34f, 14.76f)
                lineTo(16.76f, 16.18f)
                curveTo(18.15f, 14.79f, 19f, 12.88f, 19f, 10.77f)
                curveTo(19f, 8.66f, 18.15f, 6.75f, 16.76f, 5.36f)
                close()
            }
        }.build()
    }

    val Security: ImageVector by lazy {
        ImageVector.Builder(
            name = "Security",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(fill = SolidColor(Color.White)) {
                moveTo(12f, 1f)
                lineTo(3f, 5f)
                lineTo(3f, 11f)
                curveTo(3f, 16.55f, 6.84f, 21.74f, 12f, 23f)
                curveTo(17.16f, 21.74f, 21f, 16.55f, 21f, 11f)
                lineTo(21f, 5f)
                lineTo(12f, 1f)
                close()
            }
        }.build()
    }

    val Notifications: ImageVector by lazy {
        ImageVector.Builder(
            name = "Notifications",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(fill = SolidColor(Color.White)) {
                moveTo(12f, 22f)
                curveTo(13.1f, 22f, 14f, 21.1f, 14f, 20f)
                lineTo(10f, 20f)
                curveTo(10f, 21.1f, 10.9f, 22f, 12f, 22f)
                close()
                moveTo(18f, 16f)
                lineTo(18f, 11f)
                curveTo(18f, 7.93f, 16.37f, 5.36f, 13.5f, 4.68f)
                lineTo(13.5f, 4f)
                curveTo(13.5f, 3.17f, 12.83f, 2.5f, 12f, 2.5f)
                curveTo(11.17f, 2.5f, 10.5f, 3.17f, 10.5f, 4f)
                lineTo(10.5f, 4.68f)
                curveTo(7.64f, 5.36f, 6f, 7.92f, 6f, 11f)
                lineTo(6f, 16f)
                lineTo(4f, 18f)
                lineTo(4f, 19f)
                lineTo(20f, 19f)
                lineTo(20f, 18f)
                lineTo(18f, 16f)
                close()
            }
        }.build()
    }

    val CloudDone: ImageVector by lazy {
        ImageVector.Builder(
            name = "CloudDone",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(fill = SolidColor(Color.White)) {
                moveTo(19.35f, 10.04f)
                curveTo(18.67f, 6.59f, 15.64f, 4f, 12f, 4f)
                curveTo(9.11f, 4f, 6.6f, 5.64f, 5.35f, 8.04f)
                curveTo(2.34f, 8.36f, 0f, 10.91f, 0f, 14f)
                curveTo(0f, 17.31f, 2.69f, 20f, 6f, 20f)
                lineTo(19f, 20f)
                curveTo(21.76f, 20f, 24f, 17.76f, 24f, 15f)
                curveTo(24f, 12.36f, 21.95f, 10.22f, 19.35f, 10.04f)
                close()
                moveTo(10f, 17f)
                lineTo(5f, 12f)
                lineTo(6.41f, 10.59f)
                lineTo(10f, 14.17f)
                lineTo(17.59f, 6.59f)
                lineTo(19f, 8f)
                lineTo(10f, 17f)
                close()
            }
        }.build()
    }

    val Vibration: ImageVector by lazy {
        ImageVector.Builder(
            name = "Vibration",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(fill = SolidColor(Color.White)) {
                moveTo(0f, 15f)
                lineTo(2f, 15f)
                lineTo(2f, 9f)
                lineTo(0f, 9f)
                lineTo(0f, 15f)
                close()
                moveTo(3f, 17f)
                lineTo(5f, 17f)
                lineTo(5f, 7f)
                lineTo(3f, 7f)
                lineTo(3f, 17f)
                close()
                moveTo(22f, 9f)
                lineTo(22f, 15f)
                lineTo(24f, 15f)
                lineTo(24f, 9f)
                lineTo(22f, 9f)
                close()
                moveTo(19f, 17f)
                lineTo(21f, 17f)
                lineTo(21f, 7f)
                lineTo(19f, 7f)
                lineTo(19f, 17f)
                close()
                moveTo(16.5f, 3f)
                lineTo(7.5f, 3f)
                curveTo(6.67f, 3f, 6f, 3.67f, 6f, 4.5f)
                lineTo(6f, 19.5f)
                curveTo(6f, 20.33f, 6.67f, 21f, 7.5f, 21f)
                lineTo(16.5f, 21f)
                curveTo(17.33f, 21f, 18f, 20.33f, 18f, 19.5f)
                lineTo(18f, 4.5f)
                curveTo(18f, 3.67f, 17.33f, 3f, 16.5f, 3f)
                close()
                moveTo(16f, 19f)
                lineTo(8f, 19f)
                lineTo(8f, 5f)
                lineTo(16f, 5f)
                lineTo(16f, 19f)
                close()
            }
        }.build()
    }

    val Info: ImageVector by lazy {
        ImageVector.Builder(
            name = "Info",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(fill = SolidColor(Color.White)) {
                moveTo(12f, 2f)
                curveTo(6.48f, 2f, 2f, 6.48f, 2f, 12f)
                curveTo(2f, 17.52f, 6.48f, 22f, 12f, 22f)
                curveTo(17.52f, 22f, 22f, 17.52f, 22f, 12f)
                curveTo(22f, 6.48f, 17.52f, 2f, 12f, 2f)
                close()
                moveTo(13f, 17f)
                lineTo(11f, 17f)
                lineTo(11f, 11f)
                lineTo(13f, 11f)
                lineTo(13f, 17f)
                close()
                moveTo(13f, 9f)
                lineTo(11f, 9f)
                lineTo(11f, 7f)
                lineTo(13f, 7f)
                lineTo(13f, 9f)
                close()
            }
        }.build()
    }

    val DeleteForever: ImageVector by lazy {
        ImageVector.Builder(
            name = "DeleteForever",
            defaultWidth = 24.dp,
            defaultHeight = 24.dp,
            viewportWidth = 24f,
            viewportHeight = 24f
        ).apply {
            path(fill = SolidColor(Color.White)) {
                moveTo(6f, 19f)
                curveTo(6f, 20.1f, 6.9f, 21f, 8f, 21f)
                lineTo(16f, 21f)
                curveTo(17.1f, 21f, 18f, 20.1f, 18f, 19f)
                lineTo(18f, 7f)
                lineTo(6f, 7f)
                lineTo(6f, 19f)
                close()
                moveTo(8.46f, 11.88f)
                lineTo(9.87f, 10.47f)
                lineTo(12f, 12.59f)
                lineTo(14.12f, 10.47f)
                lineTo(15.53f, 11.88f)
                lineTo(13.41f, 14f)
                lineTo(15.53f, 16.12f)
                lineTo(14.12f, 17.53f)
                lineTo(12f, 15.41f)
                lineTo(9.88f, 17.53f)
                lineTo(8.47f, 16.12f)
                lineTo(10.59f, 14f)
                lineTo(8.46f, 11.88f)
                close()
                moveTo(15.5f, 4f)
                lineTo(14.5f, 3f)
                lineTo(9.5f, 3f)
                lineTo(8.5f, 4f)
                lineTo(5f, 4f)
                lineTo(5f, 6f)
                lineTo(19f, 6f)
                lineTo(19f, 4f)
                lineTo(15.5f, 4f)
                close()
            }
        }.build()
    }
}
