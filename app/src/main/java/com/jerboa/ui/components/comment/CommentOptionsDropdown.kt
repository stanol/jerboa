package com.jerboa.ui.components.comment

import android.widget.Toast
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Block
import androidx.compose.material.icons.outlined.Comment
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.material.icons.outlined.CopyAll
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Description
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Flag
import androidx.compose.material.icons.outlined.GppBad
import androidx.compose.material.icons.outlined.Link
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Restore
import androidx.compose.material3.Divider
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import com.jerboa.R
import com.jerboa.copyToClipboard
import com.jerboa.datatypes.BanFromCommunityData
import com.jerboa.ui.components.common.BanFromCommunityPopupMenuItem
import com.jerboa.ui.components.common.BanPersonPopupMenuItem
import com.jerboa.ui.components.common.PopupMenuItem
import com.jerboa.util.cascade.CascadeCenteredDropdownMenu
import it.vercruysse.lemmyapi.v0x19.datatypes.CommentView
import it.vercruysse.lemmyapi.v0x19.datatypes.Person
import it.vercruysse.lemmyapi.v0x19.datatypes.PersonId

@Composable
fun CommentOptionsDropdown(
    commentView: CommentView,
    onDismissRequest: () -> Unit,
    onCommentLinkClick: (CommentView) -> Unit,
    onPersonClick: (PersonId) -> Unit,
    onViewSourceClick: () -> Unit,
    onEditCommentClick: (CommentView) -> Unit,
    onDeleteCommentClick: (CommentView) -> Unit,
    onBlockCreatorClick: (Person) -> Unit,
    onReportClick: (CommentView) -> Unit,
    onRemoveClick: (CommentView) -> Unit,
    onBanPersonClick: (person: Person) -> Unit,
    onBanFromCommunityClick: (banData: BanFromCommunityData) -> Unit,
    isCreator: Boolean,
    canMod: Boolean,
    viewSource: Boolean,
) {
    val localClipboardManager = LocalClipboardManager.current
    val ctx = LocalContext.current

    CascadeCenteredDropdownMenu(
        expanded = true,
        onDismissRequest = onDismissRequest,
    ) {
        PopupMenuItem(
            text = stringResource(R.string.comment_node_goto_comment),
            icon = Icons.Outlined.Comment,
            onClick = {
                onDismissRequest()
                onCommentLinkClick(commentView)
            },
        )

        PopupMenuItem(
            text = stringResource(R.string.comment_node_go_to, commentView.creator.name),
            icon = Icons.Outlined.Person,
            onClick = {
                onDismissRequest()
                onPersonClick(commentView.creator.id)
            },
        )

        PopupMenuItem(
            text = stringResource(R.string.copy),
            icon = Icons.Outlined.CopyAll,
        ) {
            PopupMenuItem(
                text = stringResource(R.string.comment_node_copy_permalink),
                icon = Icons.Outlined.Link,
                onClick = {
                    onDismissRequest()
                    val permalink = commentView.comment.ap_id
                    localClipboardManager.setText(AnnotatedString(permalink))
                    Toast.makeText(
                        ctx,
                        ctx.getString(R.string.comment_node_permalink_copied),
                        Toast.LENGTH_SHORT,
                    ).show()
                },
            )
            PopupMenuItem(
                text = stringResource(R.string.comment_node_copy_comment),
                icon = Icons.Outlined.ContentCopy,
                onClick = {
                    onDismissRequest()
                    if (copyToClipboard(ctx, commentView.comment.content, "comment")) {
                        Toast.makeText(
                            ctx,
                            ctx.getString(R.string.comment_node_comment_copied),
                            Toast.LENGTH_SHORT,
                        ).show()
                    } else {
                        Toast.makeText(
                            ctx,
                            ctx.getString(R.string.generic_error),
                            Toast.LENGTH_SHORT,
                        ).show()
                    }
                },
            )
        }

        PopupMenuItem(
            text =
                if (viewSource) {
                    stringResource(R.string.comment_node_view_original)
                } else {
                    stringResource(R.string.comment_node_view_source)
                },
            icon = Icons.Outlined.Description,
            onClick = {
                onDismissRequest()
                onViewSourceClick()
            },
        )

        Divider()

        if (isCreator) {
            PopupMenuItem(
                text = stringResource(R.string.comment_node_edit),
                icon = Icons.Outlined.Edit,
                onClick = {
                    onDismissRequest()
                    onEditCommentClick(commentView)
                },
            )

            if (commentView.comment.deleted) {
                PopupMenuItem(
                    text = stringResource(R.string.comment_node_restore),
                    icon = Icons.Outlined.Restore,
                    onClick = {
                        onDismissRequest()
                        onDeleteCommentClick(commentView)
                    },
                )
            } else {
                PopupMenuItem(
                    text = stringResource(R.string.comment_node_delete),
                    icon = Icons.Outlined.Delete,
                    onClick = {
                        onDismissRequest()
                        onDeleteCommentClick(commentView)
                    },
                )
            }
        } else {
            PopupMenuItem(
                text = stringResource(R.string.comment_node_block, commentView.creator.name),
                icon = Icons.Outlined.Block,
                onClick = {
                    onDismissRequest()
                    onBlockCreatorClick(commentView.creator)
                },
            )
            PopupMenuItem(
                text = stringResource(R.string.comment_node_report_comment),
                icon = Icons.Outlined.Flag,
                onClick = {
                    onDismissRequest()
                    onReportClick(commentView)
                },
            )

            if (canMod) {
                Divider()
                val (removeText, removeIcon) =
                    if (commentView.comment.removed) {
                        Pair(stringResource(R.string.restore_comment), Icons.Outlined.Restore)
                    } else {
                        Pair(stringResource(R.string.remove_comment), Icons.Outlined.GppBad)
                    }

                PopupMenuItem(
                    text = removeText,
                    icon = removeIcon,
                    onClick = {
                        onDismissRequest()
                        onRemoveClick(commentView)
                    },
                )
                BanPersonPopupMenuItem(commentView.creator, onDismissRequest, onBanPersonClick)

                // Only show ban from community button if its a local community
                if (commentView.community.local) {
                    BanFromCommunityPopupMenuItem(
                        BanFromCommunityData(
                            person = commentView.creator,
                            community = commentView.community,
                            banned = commentView.creator_banned_from_community,
                        ),
                        onDismissRequest,
                        onBanFromCommunityClick,
                    )
                }
            }
        }
    }
}
