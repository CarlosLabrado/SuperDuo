package it.jaschke.alexandria;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import it.jaschke.alexandria.data.AlexandriaContract;
import it.jaschke.alexandria.data.Book;
import it.jaschke.alexandria.misc.Utility;
import it.jaschke.alexandria.services.BookService;


public class AddBook extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final String TAG = "INTENT_TO_SCAN_ACTIVITY";
    private static final String SAVED_BOOK = "PARCELABLE_SAVED_BOOK";
    private final int LOADER_ID = 1;
    private View rootView;
    private final String EAN_CONTENT = "eanContent";
    private static final String SCAN_FORMAT = "scanFormat";
    private static final String SCAN_CONTENTS = "scanContents";

    private String mScanFormat = "Format:";
    private String mScanContents = "Contents:";

    private static final int REQUEST_CODE_SCAN = 77;

    boolean isConnected;

    Book mBook;

    Context mContext;

    public AddBook() {
    }

    @Bind(R.id.ean)
    EditText mEAN;

    @Bind(R.id.bookInfoContainer)
    LinearLayout mInfoContainer;
    @Bind(R.id.authors)
    TextView mAuthorsTextView;
    @Bind(R.id.bookTitle)
    TextView mBookTitle;
    @Bind(R.id.bookSubTitle)
    TextView mBookSubTitle;
    @Bind(R.id.categories)
    TextView mCategories;

    @Bind(R.id.bookCover)
    ImageView mBookCover;

    @OnClick(R.id.scan_button)
    public void scanButtonClicked() {
        Intent intent = new Intent(getActivity(), ScanISBNActivity.class);
        startActivityForResult(intent, 77);
        clearFields();
    }

    @OnClick(R.id.buttonAdd)
    public void addButtonClicked() {
        String ean = mEAN.getText().toString();
        //catch isbn10 numbers
        if (ean.length() == 10 && !ean.startsWith("978")) {
            ean = "978" + ean;
        }
        if (ean.length() < 13) {
            clearFields();
            return;
        }
        //Once we have an ISBN, start a book intent
        if (isConnected) {
            Intent bookIntent = new Intent(getActivity(), BookService.class);
            bookIntent.putExtra(BookService.EAN, ean);
            bookIntent.setAction(BookService.FETCH_BOOK);
            getActivity().startService(bookIntent);
            AddBook.this.restartLoader();
        } else {
            Toast.makeText(getActivity(), R.string.no_internet, Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(SAVED_BOOK, mBook);
        if (mEAN != null) {
            outState.putString(EAN_CONTENT, mEAN.getText().toString());
        }
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_add_book, container, false);
        ButterKnife.bind(this, rootView);

        if (savedInstanceState != null) {
            mBook = savedInstanceState.getParcelable(SAVED_BOOK);
            populateView(mBook);
        }

        isConnected = Utility.isNetworkAvailable(getContext());

        if (!isConnected) {
            Snackbar.make(rootView, R.string.no_internet, Snackbar.LENGTH_INDEFINITE).show();
        }

        rootView.findViewById(R.id.save_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mEAN.setText("");
            }
        });

        rootView.findViewById(R.id.delete_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent bookIntent = new Intent(getActivity(), BookService.class);
                bookIntent.putExtra(BookService.EAN, mEAN.getText().toString());
                bookIntent.setAction(BookService.DELETE_BOOK);
                getActivity().startService(bookIntent);
                mEAN.setText("");
            }
        });

        if (savedInstanceState != null) {
            mEAN.setText(savedInstanceState.getString(EAN_CONTENT));
            mEAN.setHint("");
        }

        return rootView;
    }

    private void restartLoader() {
        Loader loader = getLoaderManager().getLoader(LOADER_ID);
        if (loader != null && !loader.isReset()) {
            getLoaderManager().restartLoader(LOADER_ID, null, this);
        } else {
            getLoaderManager().initLoader(LOADER_ID, null, this);
        }
    }

    @Override
    public android.support.v4.content.Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (mEAN.getText().length() == 0) {
            return null;
        }
        String eanStr = mEAN.getText().toString();
        if (eanStr.length() == 10 && !eanStr.startsWith("978")) {
            eanStr = "978" + eanStr;
        }
        return new CursorLoader(
                getActivity(),
                AlexandriaContract.BookEntry.buildFullBookUri(Long.parseLong(eanStr)),
                null,
                null,
                null,
                null
        );
    }

    @Override
    public void onLoadFinished(android.support.v4.content.Loader<Cursor> loader, Cursor data) {
        Log.d("ON LOAD FINISHED", "service ENDED ");

        if (!data.moveToFirst()) {
            return;
        }

        String bookTitle = data.getString(data.getColumnIndex(AlexandriaContract.BookEntry.TITLE));
        String bookSubTitle = data.getString(data.getColumnIndex(AlexandriaContract.BookEntry.SUBTITLE));
        String authors = data.getString(data.getColumnIndex(AlexandriaContract.AuthorEntry.AUTHOR));
        String imgUrl = data.getString(data.getColumnIndex(AlexandriaContract.BookEntry.IMAGE_URL));
        String categories = data.getString(data.getColumnIndex(AlexandriaContract.CategoryEntry.CATEGORY));

        mBook = new Book(bookTitle, bookSubTitle, authors, imgUrl, categories);

        populateView(mBook);
    }

    private void populateView(Book book) {
        mInfoContainer.setVisibility(View.VISIBLE);

        if (book.getTitle() != null) {
            mBookTitle.setText(book.getTitle());
        }

        if (book.getSubTitle() != null) {
            mBookSubTitle.setText(book.getSubTitle());
        }

        String[] authorsArr;
        if (book.getAuthors() != null && !book.getAuthors().equals("")) {
            authorsArr = book.getAuthors().split(",");
            mAuthorsTextView.setLines(authorsArr.length);
            mAuthorsTextView.setText(book.getAuthors().replace(",", "\n"));
        } else {
            mAuthorsTextView.setText(R.string.no_author_found);
        }

        Glide.with(this)
                .load(book.getImgUrl())
                .fitCenter()
                .placeholder(R.drawable.ic_launcher)
                .error(R.drawable.ic_launcher)
                .into(mBookCover);

        if (book.getCategories() != null) {
            mCategories.setText(book.getCategories());
        }
    }

    @Override
    public void onLoaderReset(android.support.v4.content.Loader<Cursor> loader) {
        Log.d("RESETTED?", "loader reset ");

    }

    private void clearFields() {
        mBookTitle.setText("");
        mBookSubTitle.setText("");
        mAuthorsTextView.setText("");
        mCategories.setText("");

        mInfoContainer.setVisibility(View.INVISIBLE);

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        activity.setTitle(R.string.scan);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == getActivity().RESULT_OK) {
            if (requestCode == REQUEST_CODE_SCAN) {
                mEAN.setText(data.getStringExtra(ScanISBNActivity.ISBN_EXTRA));
                addButtonClicked();
            }
        }
    }
}
