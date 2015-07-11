package com.thejakeofink.mountainviewgirlscamp;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;


public class StudyGuideFragment extends Fragment implements View.OnClickListener {

    public static final int FAITH = 1;
    public static final int REVELATION = 2;
    public static final int TEMPTATION = 3;
    public static final int THEME = 4;
    public static final String KEY_FILE_TO_LOAD = "fileToLoad";

    protected static final int HEADER_ITEM = 0;
    protected static final int THEME_HEADER_ITEM = 1;
    protected static final int STANDARD_ITEM = 2;

    Button studyDone;
    RecyclerView contentView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.activity_study_guide, container, false);

        studyDone = (Button) rootView.findViewById(R.id.btn_study_done);
        contentView = (RecyclerView) rootView.findViewById(R.id.study_guide_content);

        contentView.setHasFixedSize(false);
        LinearLayoutManager llm = new LinearLayoutManager(getActivity());
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        contentView.setLayoutManager(llm);

        studyDone.setOnClickListener(this);

        StudyGuideAdapter adapter;

        Bundle bundle = getArguments();
        if (bundle != null) {
            adapter = new StudyGuideAdapter(getActivity(), bundle.containsKey(KEY_FILE_TO_LOAD) ? bundle.getInt(KEY_FILE_TO_LOAD) : 0);
        } else {
            adapter = new StudyGuideAdapter(getActivity(), 0);
            studyDone.setVisibility(View.GONE);
        }

        contentView.setAdapter(adapter);

        return rootView;
    }

    @Override
    public void onClick(View v) {
        if (v == studyDone) {
            getFragmentManager().popBackStack();
        }
    }

    public class StudyGuideAdapter extends RecyclerView.Adapter<StudyGuideFragment.ImageViewHolder> {

        String[] headers;
        String[] paragraphs;
        String title;
        String theme;
        boolean isTheme = false;

        public StudyGuideAdapter(Context context, int studyGuideId) {

            int headerId;
            int paragraphId;
            int titleId;

            switch (studyGuideId) {
                case FAITH:
                    headerId = R.array.faith_friendships_topics;
                    paragraphId = R.array.faith_friendships_body;
                    titleId = R.string.faith_friendships;
                    break;
                case REVELATION:
                    headerId = R.array.personal_rev_topics;
                    paragraphId = R.array.personal_rev_body;
                    titleId = R.string.personal_rev;
                    break;
                case TEMPTATION:
                    headerId = R.array.temptation_topics;
                    paragraphId = R.array.temptation_body;
                    titleId = R.string.tempation;
                    break;
                case THEME:
                    headerId = R.array.faith_friendships_topics;
                    paragraphId = R.array.faith_friendships_body;
                    titleId = R.string.theme;
                    theme = context.getResources().getString(R.string.actual_theme);
                    isTheme = true;
                    break;
                default:
                    headerId = R.array.quotes_topics;
                    paragraphId = R.array.quotes_body;
                    titleId = R.string.quotes;
                    break;
            }


            headers = context.getResources().getStringArray(headerId);
            paragraphs = context.getResources().getStringArray(paragraphId);
            title = context.getResources().getString(titleId);
        }

        @Override
        public int getItemViewType(int position) {
            if (position == 0) {
                if (isTheme) {
                    return THEME_HEADER_ITEM;
                }
                return HEADER_ITEM;
            }
            return STANDARD_ITEM;
        }

        @Override
        public ImageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v;

            switch (viewType) {
                case HEADER_ITEM:
                    v = LayoutInflater.from(parent.getContext()).inflate(R.layout.study_guide_title_item, parent, false);
                    break;
                case THEME_HEADER_ITEM:
                    v = LayoutInflater.from(parent.getContext()).inflate(R.layout.theme_header_item, parent, false);
                    break;
                case STANDARD_ITEM:
                default:
                    v = LayoutInflater.from(parent.getContext()).inflate(R.layout.study_item, parent, false);
                    break;
            }

            return new ImageViewHolder(v, viewType);
        }

        @Override
        public void onBindViewHolder(ImageViewHolder holder, int position) {

            switch (holder.viewType) {
                case THEME_HEADER_ITEM:
                    holder.vBody.setText(Html.fromHtml(theme), TextView.BufferType.SPANNABLE);
                case HEADER_ITEM:
                    holder.vTitle.setText(title);
                    break;
                case STANDARD_ITEM:
                default:
                    holder.vTitle.setText(headers[position - 1]);
                    holder.vBody.setText(paragraphs[position - 1]);
                    break;
            }
        }

        @Override
        public int getItemCount() {
            return headers.length + 1;
        }
    }

    public static class ImageViewHolder extends RecyclerView.ViewHolder {
        protected TextView vTitle;
        protected TextView vBody;
        protected View itemView;
        protected int viewType;

        public ImageViewHolder(View itemView, int viewType) {
            super(itemView);
            this.itemView = itemView;
            this.viewType = viewType;

            switch (viewType) {
                case HEADER_ITEM:
                    vTitle = (TextView) itemView.findViewById(R.id.tv_guide_title);
                    vBody = null;
                    break;
                case THEME_HEADER_ITEM:
                    vTitle = (TextView) itemView.findViewById(R.id.tv_theme_title);
                    vBody = (TextView) itemView.findViewById(R.id.tv_actual_theme);
                    break;
                case STANDARD_ITEM:
                default:
                    vTitle = (TextView) itemView.findViewById(R.id.tv_guide_header);
                    vBody = (TextView) itemView.findViewById(R.id.tv_study_guide);
                    break;
            }
        }
    }
}
