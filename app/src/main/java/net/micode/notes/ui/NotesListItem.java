/*
 * Copyright (c) 2010-2011, The MiCode Open Source Community (www.micode.net)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.micode.notes.ui;

import android.content.Context;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import net.micode.notes.R;
import net.micode.notes.data.Notes;
import net.micode.notes.tool.DataUtils;
import net.micode.notes.tool.ResourceParser.NoteItemBgResources;

//创建便签列表项目选项
public class NotesListItem extends LinearLayout {
    private ImageView mAlert;//闹钟图片
    private TextView mTitle;//标题
    private TextView mTime;//时间
    private TextView mCallName;
    private NoteItemData mItemData;//标签数据
    private CheckBox mCheckBox;//打勾框
/*初始化基本信息*/
    public NotesListItem(Context context) {
        super(context);//super()它的主要作用是调整调用父类构造函数的顺序
        inflate(context, R.layout.note_item, this);//Inflate可用于将一个xml中定义的布局控件找出来,这里的xml是r。layout
        //findViewById用于从contentView中查找指定ID的View，转换出来的形式根据需要而定;
        mAlert = (ImageView) findViewById(R.id.iv_alert_icon);
        mTitle = (TextView) findViewById(R.id.tv_title);
        mTime = (TextView) findViewById(R.id.tv_time);
        mCallName = (TextView) findViewById(R.id.tv_name);
        mCheckBox = (CheckBox) findViewById(android.R.id.checkbox);
    }
 ///根据data的属性对各个控件的属性的控制，主要是可见性Visibility，内容setText，格式setTextAppearance
    public void bind(Context context, NoteItemData data, boolean choiceMode, boolean checked) {
        if (choiceMode && data.getType() == Notes.TYPE_NOTE) {
            mCheckBox.setVisibility(View.VISIBLE);///设置可见行为可见
            mCheckBox.setChecked(checked);///格子打钩
        } else {
            mCheckBox.setVisibility(View.GONE);
        }

        mItemData = data;
///设置控件属性，一共三种情况，由data的id和父id是否与保存到文件夹的id一致来决定
        if (data.getId() == Notes.ID_CALL_RECORD_FOLDER) {
            mCallName.setVisibility(View.GONE);
            mAlert.setVisibility(View.VISIBLE);
//设置该textview的style
            mTitle.setTextAppearance(context, R.style.TextAppearancePrimaryItem);
//settext为设置内容
            mTitle.setText(context.getString(R.string.call_record_folder_name)
                    + context.getString(R.string.format_folder_files_count, data.getNotesCount()));
            mAlert.setImageResource(R.drawable.call_record);
        } else if (data.getParentId() == Notes.ID_CALL_RECORD_FOLDER) {
            mCallName.setVisibility(View.VISIBLE);
            mCallName.setText(data.getCallName());
            mTitle.setTextAppearance(context,R.style.TextAppearanceSecondaryItem);
            mTitle.setText(DataUtils.getFormattedSnippet(data.getSnippet()));
///关于闹钟的设置
            if (data.hasAlert()) {//图片来源的设置
                mAlert.setImageResource(R.drawable.clock);
                mAlert.setVisibility(View.VISIBLE);
            } else {
                mAlert.setVisibility(View.GONE);
            }
        } else {
            mCallName.setVisibility(View.GONE);
            mTitle.setTextAppearance(context, R.style.TextAppearancePrimaryItem);
///设置title格式
            if (data.getType() == Notes.TYPE_FOLDER) {
                mTitle.setText(data.getSnippet()
                        + context.getString(R.string.format_folder_files_count,
                                data.getNotesCount()));
                mAlert.setVisibility(View.GONE);
            } else {
                mTitle.setText(DataUtils.getFormattedSnippet(data.getSnippet()));
                if (data.hasAlert()) {
                    mAlert.setImageResource(R.drawable.clock);///设置图片来源
                    mAlert.setVisibility(View.VISIBLE);
                } else {
                    mAlert.setVisibility(View.GONE);
                }
            }
        }
        ///设置内容，获取相关时间，从data里编辑的日期中获取
        mTime.setText(DateUtils.getRelativeTimeSpanString(data.getModifiedDate()));

        setBackground(data);
    }

    //根据data的文件属性来设置背景
    private void setBackground(NoteItemData data) {
        int id = data.getBgColorId();
        if (data.getType() == Notes.TYPE_NOTE) {
            if (data.isSingle() || data.isOneFollowingFolder()) {
                setBackgroundResource(NoteItemBgResources.getNoteBgSingleRes(id));
            } else if (data.isLast()) {
                setBackgroundResource(NoteItemBgResources.getNoteBgLastRes(id));
            } else if (data.isFirst() || data.isMultiFollowingFolder()) {
                setBackgroundResource(NoteItemBgResources.getNoteBgFirstRes(id));
            } else {
                setBackgroundResource(NoteItemBgResources.getNoteBgNormalRes(id));
            }
        } else {
            setBackgroundResource(NoteItemBgResources.getFolderBgRes());
        }
    }

    public NoteItemData getItemData() {
        return mItemData;
    }
}
