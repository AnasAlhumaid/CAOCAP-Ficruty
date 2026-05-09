package com.example.cocap.ui.canvas

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import java.util.UUID

private const val MIN_SCALE = 0.4f
private const val MAX_SCALE = 3.0f

enum class NodeType {
    Standard,
    WebPreview
}

data class CanvasNode(
    val id: UUID = UUID.randomUUID(),
    var position: Offset,
    val title: String,
    val subtitle: String? = null,
    val icon: ImageVector? = null,
    val action: NodeAction? = null,
    val type: NodeType = NodeType.Standard,
    val connectedTo: UUID? = null,
    val connectionColor: Color = Color.White.copy(alpha = 0.2f),
    val isDraggable: Boolean = true,
    val isClickable: Boolean = true
)

enum class NodeAction {
    NavigateHome,
    RetryOnboarding,
    CreateNewProject,
    OpenProjectExplorer,
    OpenSettings,
    OpenProfile
}

object CanvasPresets {
    fun onboardingNodes(): List<CanvasNode> {
        val welcomeId = UUID.randomUUID()
        val canvasId = UUID.randomUUID()
        val homeId = UUID.randomUUID()
        
        return listOf(
            CanvasNode(
                id = welcomeId,
                position = Offset(-260f, -140f),
                title = "Welcome to CAOCAP",
                subtitle = "The spatial landscape for agentic software design.",
                icon = Icons.Default.RocketLaunch,
                connectedTo = canvasId,
                connectionColor = Color(0xFF64B5F6).copy(alpha = 0.4f)
            ),
            CanvasNode(
                id = canvasId,
                position = Offset(40f, 90f),
                title = "Infinite Canvas",
                subtitle = "Pan and zoom freely to map your software ideas.",
                icon = Icons.Default.Description,
                connectedTo = homeId,
                connectionColor = Color(0xFF81C784).copy(alpha = 0.4f)
            ),
            CanvasNode(
                id = homeId,
                position = Offset(350f, -40f),
                title = "Go Home",
                subtitle = "Open your workspace hub.",
                icon = Icons.Default.Home,
                action = NodeAction.NavigateHome
            )
        )
    }

    fun homeNodes(): List<CanvasNode> = listOf(
        CanvasNode(position = Offset(-280f, -180f), title = "Profile", subtitle = "Manage your account.", icon = Icons.Default.AccountCircle, action = NodeAction.OpenProfile),
        CanvasNode(position = Offset(40f, -120f), title = "Projects", subtitle = "Browse your projects.", icon = Icons.Default.Folder, action = NodeAction.OpenProjectExplorer),
        CanvasNode(position = Offset(-120f, 140f), title = "Settings", subtitle = "App configuration.", icon = Icons.Default.Settings, action = NodeAction.OpenSettings),
        CanvasNode(position = Offset(260f, 120f), title = "New Project", subtitle = "Start building.", icon = Icons.Default.Description, action = NodeAction.CreateNewProject),
        CanvasNode(position = Offset(60f, 360f), title = "Retry Onboarding", subtitle = "Run guided tour again.", icon = Icons.Default.RocketLaunch, action = NodeAction.RetryOnboarding)
    )

    fun projectNodes(): List<CanvasNode> = listOf(
        CanvasNode(position = Offset(-320f, -180f), title = "SRS", subtitle = "Write your requirements.", icon = Icons.Default.Description),
        CanvasNode(position = Offset(-40f, -30f), title = "HTML", subtitle = "Build page structure.", icon = Icons.Default.Description),
        CanvasNode(position = Offset(-40f, 170f), title = "CSS", subtitle = "Style your UI.", icon = Icons.Default.Description),
        CanvasNode(position = Offset(250f, 40f), title = "JavaScript", subtitle = "Add interactions.", icon = Icons.Default.Description),
        CanvasNode(position = Offset(520f, -40f), title = "Live Preview", subtitle = "Rendered output.", icon = Icons.Default.Visibility, type = NodeType.WebPreview)
    )
}

@Composable
fun InfiniteCanvasScreen(
    modifier: Modifier = Modifier,
    initialNodes: List<CanvasNode>,
    onNodeAction: (NodeAction) -> Unit = {}
) {
    val nodes = remember(initialNodes) {
        mutableStateListOf<CanvasNode>().also { it.addAll(initialNodes) }
    }

    var viewportScale by remember { mutableStateOf(1f) }
    var viewportOffset by remember { mutableStateOf(Offset.Zero) }
    var draggingNodeId by remember { mutableStateOf<UUID?>(null) }
    var topNodeId by remember { mutableStateOf<UUID?>(null) }

    val transformState = rememberTransformableState { zoomChange, panChange, _ ->
        viewportScale = (viewportScale * zoomChange).coerceIn(MIN_SCALE, MAX_SCALE)
        viewportOffset += panChange
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF0D0D0D))
            .transformable(state = transformState)
            .pointerInput(Unit) {
                detectDragGestures { change, dragAmount ->
                    change.consume()
                    viewportOffset += dragAmount
                }
            }
            .pointerInput(Unit) {
                detectTapGestures(
                    onTap = { offset ->
                        val canvasCenter = Offset(size.width / 2f, size.height / 2f)
                        val hitNode = findHitNode(
                            pointer = offset,
                            nodes = nodes,
                            viewportScale = viewportScale,
                            viewportOffset = viewportOffset,
                            canvasCenter = canvasCenter
                        )
                        if (hitNode?.isClickable == true) {
                            hitNode.action?.let(onNodeAction)
                        }
                    }
                )
            }
    ) {
        // 1. Background Grid & Connections
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawDottedBackground(viewportOffset, viewportScale, size)
            drawConnections(nodes, viewportScale, viewportOffset, center)
        }

        // 2. Nodes Layer
        nodes.forEach { node ->
            val isDragging = draggingNodeId == node.id
            val isOnTop = topNodeId == node.id
            
            NodeCard(
                node = node,
                isDragging = isDragging,
                scale = viewportScale,
                modifier = Modifier
                    .align(Alignment.Center)
                    .zIndex(if (isOnTop || isDragging) 10f else 1f)
                    .graphicsLayer {
                        // All transformations in graphicsLayer to avoid layout passes
                        translationX = node.position.x * viewportScale + viewportOffset.x
                        translationY = node.position.y * viewportScale + viewportOffset.y
                        
                        // Scale the whole card visually instead of re-calculating DP sizes
                        scaleX = viewportScale.coerceAtLeast(0.7f)
                        scaleY = viewportScale.coerceAtLeast(0.7f)
                    }
                    .pointerInput(node.id, node.isDraggable) {
                        if (node.isDraggable) {
                            detectDragGestures(
                                onDragStart = {
                                    draggingNodeId = node.id
                                    topNodeId = node.id
                                },
                                onDrag = { change, dragAmount ->
                                    change.consume()
                                    val worldDelta = dragAmount / viewportScale
                                    val idx = nodes.indexOfFirst { it.id == node.id }
                                    if (idx >= 0) {
                                        nodes[idx] = nodes[idx].copy(position = nodes[idx].position + worldDelta)
                                    }
                                },
                                onDragEnd = { draggingNodeId = null },
                                onDragCancel = { draggingNodeId = null }
                            )
                        }
                    }
            )
        }

        ScaleBadge(
            scale = viewportScale,
            modifier = Modifier
                .align(Alignment.TopStart)
                .offset(16.dp, 16.dp)
        )
    }
}

@Composable
private fun NodeCard(
    node: CanvasNode,
    isDragging: Boolean,
    scale: Float,
    modifier: Modifier = Modifier
) {
    // Base sizes that don't change with scale (scaling handled by graphicsLayer)
    val width = if (node.type == NodeType.WebPreview) 260.dp else 200.dp
    val height = if (node.type == NodeType.WebPreview) 170.dp else 115.dp
    
    Surface(
        modifier = modifier.size(width = width, height = height),
        shape = RoundedCornerShape(20.dp),
        color = if (isDragging) Color(0xFF333333) else Color(0xFF1E1E1E),
        tonalElevation = if (isDragging) 12.dp else 2.dp,
        border = BorderStroke(1.dp, if (isDragging) Color.White.copy(0.4f) else Color(0xFF444444)),
        shadowElevation = if (isDragging) 12.dp else 4.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 14.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (node.icon != null) {
                    Icon(
                        imageVector = node.icon, 
                        contentDescription = null, 
                        tint = if (node.type == NodeType.WebPreview) Color(0xFF64B5F6) else Color.White, 
                        modifier = Modifier.size(20.dp)
                    )
                }
                Text(
                    text = " ${node.title}",
                    color = Color.White,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
            }
            
            if (node.type == NodeType.WebPreview) {
                Box(
                    modifier = Modifier
                        .padding(top = 10.dp)
                        .weight(1f)
                        .background(Color(0xFF121212), RoundedCornerShape(8.dp))
                        .padding(8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Live Preview", color = Color(0xFF888888), style = MaterialTheme.typography.labelSmall)
                }
            } else if (!node.subtitle.isNullOrBlank()) {
                Text(
                    text = node.subtitle,
                    color = Color(0xFFAAAAAA),
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(top = 8.dp),
                    maxLines = 2
                )
            }
        }
    }
}

@Composable
private fun ScaleBadge(scale: Float, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        color = Color.Black.copy(0.7f),
        border = BorderStroke(1.dp, Color.White.copy(0.2f))
    ) {
        Text(
            text = "Scale: %.2fx".format(scale),
            color = Color.White,
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
        )
    }
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawDottedBackground(
    viewportOffset: Offset,
    viewportScale: Float,
    size: Size
) {
    val spacing = 48f * viewportScale
    if (spacing <= 10f) return // Hide grid if too small for performance
    
    val radius = (2f * viewportScale).coerceAtLeast(1.5f)
    val centerX = size.width / 2f
    val centerY = size.height / 2f
    
    val startX = ((viewportOffset.x + centerX) % spacing) - spacing
    val startY = ((viewportOffset.y + centerY) % spacing) - spacing
    
    val cols = (size.width / spacing).toInt() + 4
    val rows = (size.height / spacing).toInt() + 4

    for (row in 0 until rows) {
        val y = startY + row * spacing
        for (col in 0 until cols) {
            val x = startX + col * spacing
            drawCircle(
                color = Color(0xFF252525), 
                radius = radius, 
                center = Offset(x, y)
            )
        }
    }
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawConnections(
    nodes: List<CanvasNode>,
    viewportScale: Float,
    viewportOffset: Offset,
    center: Offset
) {
    nodes.forEach { node ->
        node.connectedTo?.let { targetId ->
            val target = nodes.find { it.id == targetId }
            if (target != null) {
                val from = node.position * viewportScale + viewportOffset + center
                val to = target.position * viewportScale + viewportOffset + center
                
                drawLine(
                    color = node.connectionColor,
                    start = from,
                    end = to,
                    strokeWidth = (3f * viewportScale).coerceAtLeast(2f),
                    cap = androidx.compose.ui.graphics.StrokeCap.Round
                )
            }
        }
    }
}

private fun findHitNode(
    pointer: Offset,
    nodes: List<CanvasNode>,
    viewportScale: Float,
    viewportOffset: Offset,
    canvasCenter: Offset
): CanvasNode? {
    return nodes.lastOrNull { node ->
        val visualScale = viewportScale.coerceAtLeast(0.7f)
        val baseWidth = if (node.type == NodeType.WebPreview) 260f else 200f
        val baseHeight = if (node.type == NodeType.WebPreview) 170f else 115f
        
        // Match the graphicsLayer scaling for hit testing
        val nodeHalfWidth = (baseWidth * visualScale) / 2f
        val nodeHalfHeight = (baseHeight * visualScale) / 2f
        val centerPos = node.position * viewportScale + viewportOffset + canvasCenter
        
        pointer.x in (centerPos.x - nodeHalfWidth)..(centerPos.x + nodeHalfWidth) &&
        pointer.y in (centerPos.y - nodeHalfHeight)..(centerPos.y + nodeHalfHeight)
    }
}
