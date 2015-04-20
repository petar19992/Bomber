package games.voidsoft.org.bomber.costumContextMenu;

import android.graphics.drawable.Drawable;

/**
 * Created by Petar on 4/20/2015.
 * */
 public class ContextMenuItem {

 Drawable drawable;
 String text;

 public ContextMenuItem(Drawable drawable, String text) {
 super();
 this.drawable = drawable;
 this.text = text;
 }

 public Drawable getDrawable() {
 return drawable;
 }

 public void setDrawable(Drawable drawable) {
 this.drawable = drawable;
 }

 public String getText() {
 return text;
 }

 public void setText(String text) {
 this.text = text;
 }

 }