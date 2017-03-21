/* Copyright 2017 Andrew Dawson
 *
 * This file is part of Tusky.
 *
 * Tusky is free software: you can redistribute it and/or modify it under the terms of the GNU
 * Lesser General Public License as published by the Free Software Foundation, either version 3 of
 * the License, or (at your option) any later version.
 *
 * Tusky is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even
 * the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser
 * General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License along with Tusky. If
 * not, see <http://www.gnu.org/licenses/>. */

package com.keylesspalace.tusky;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.keylesspalace.tusky.entity.Account;
import com.pkmmte.view.CircularImageView;
import com.squareup.picasso.Picasso;

import java.util.HashSet;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;

class BlocksAdapter extends AccountAdapter {
    private static final int VIEW_TYPE_BLOCKED_USER = 0;
    private static final int VIEW_TYPE_FOOTER = 1;

    private Set<Integer> unblockedAccountPositions;

    BlocksAdapter(AccountActionListener accountActionListener) {
        super(accountActionListener);
        unblockedAccountPositions = new HashSet<>();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            default:
            case VIEW_TYPE_BLOCKED_USER: {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_blocked_user, parent, false);
                return new BlockedUserViewHolder(view);
            }
            case VIEW_TYPE_FOOTER: {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_footer, parent, false);
                return new FooterViewHolder(view);
            }
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        if (position < accountList.size()) {
            BlockedUserViewHolder holder = (BlockedUserViewHolder) viewHolder;
            holder.setupWithAccount(accountList.get(position));
            boolean blocked = !unblockedAccountPositions.contains(position);
            holder.setupActionListener(accountActionListener, blocked, position);
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (position == accountList.size()) {
            return VIEW_TYPE_FOOTER;
        } else {
            return VIEW_TYPE_BLOCKED_USER;
        }
    }

    void setBlocked(boolean blocked, int position) {
        if (blocked) {
            unblockedAccountPositions.remove(position);
        } else {
            unblockedAccountPositions.add(position);
        }
        notifyItemChanged(position);
    }

    static class BlockedUserViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.blocked_user_avatar) CircularImageView avatar;
        @BindView(R.id.blocked_user_username) TextView username;
        @BindView(R.id.blocked_user_display_name) TextView displayName;
        @BindView(R.id.blocked_user_unblock) ImageButton unblock;

        private String id;

        BlockedUserViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        void setupWithAccount(Account account) {
            id = account.id;
            displayName.setText(account.getDisplayName());
            String format = username.getContext().getString(R.string.status_username_format);
            String formattedUsername = String.format(format, account.username);
            username.setText(formattedUsername);
            Picasso.with(avatar.getContext())
                    .load(account.avatar)
                    .error(R.drawable.avatar_error)
                    .placeholder(R.drawable.avatar_default)
                    .into(avatar);
        }

        void setupActionListener(final AccountActionListener listener, final boolean blocked,
                final int position) {
            unblock.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onBlock(!blocked, id, position);
                }
            });
            avatar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onViewAccount(id);
                }
            });
        }
    }
}
