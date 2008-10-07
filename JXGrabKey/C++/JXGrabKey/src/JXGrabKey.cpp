/*	Copyright 2008  Edwin Stang (edwinstang@gmail.com), 
 *
 *  This file is part of JXGrabKey.
 *
 *  JXGrabKey is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  JXGrabKey is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with JXGrabKey.  If not, see <http://www.gnu.org/licenses/>.
 */

#include "JXGrabKey.h"
#include <X11/Xlib.h>
#include <stdio.h>
#include <vector>

using namespace std;

struct KeyStruct {
    int id;
    KeyCode key;
    Mask mask;
};

Display *dpy;
Window root;
bool isListening = false;
bool error = false;
vector<KeyStruct> keys;

JNIEXPORT void JNICALL Java_jxgrabkey_JXGrabKey_clean
  (JNIEnv *_env, jobject _obj){
    while(!isListening && !error){
        sleep(10);
    }
    if(error){
        return;
    }

    for(int i = 0; i < keys.size(); i++){
        Java_jxgrabkey_JXGrabKey_unregisterHotKey(_env, _obj, keys.at(i).id);
    }
}

JNIEXPORT void JNICALL Java_jxgrabkey_JXGrabKey_registerHotkey__III
  (JNIEnv *_env, jobject _obj, jint _id, jint _mask, jint _key){
    while(!isListening && !error){
        sleep(10);
    }
    if(error){
        return;
    }
    
    struct KeyStruct key;
    key.id = _id;
    key.key = XKeysymToKeycode(dpy, _key);
    key.mask = _mask;
    
    keys.push_back(key);

    XGrabKey(dpy, key.key , key.mask, root, true, GrabModeAsync, GrabModeAsync);
}

JNIEXPORT void JNICALL Java_jxgrabkey_JXGrabKey_unregisterHotKey
  (JNIEnv *_env, jobject _obj, jint _id){
    while(!isListening && !error){
        sleep(10);
    }
    if(error){
        return;
    }
    
    for(int i = 0; i < keys.size(); i++){
        if(keys.at(i).id == _id){
            XUngrabKey(dpy, keys.at(i).key, keys.at(i).mask, root);
            keys.erase(keys.begin()+i);
            break;
        }
    }
}

JNIEXPORT void JNICALL Java_jxgrabkey_JXGrabKey_listen
  (JNIEnv *_env, jobject _obj){    
    
    if(isListening){
        printf("WARNING: already listening, aborting\n");
        return;
    }
    
    jclass cls = _env->FindClass("jxgrabkey/JXGrabKey");
    if(cls == NULL){
        printf("ERROR: cannot find class jxgrabkey.JXGrabKey\n");
        error = true;
        return;
    }
    
    jmethodID mid = _env->GetStaticMethodID(cls, "fireKeyEvent", "(I)V" );
    if(mid ==0){
        printf("ERROR: cannot find method fireKeyEvent(int)\n");
        error = true;
        return;
    }

    dpy = XOpenDisplay(NULL);
    
    if(!dpy){
        printf("ERROR: cannot find method fireKeyEvent(int)\n");
        error = true;
        return;
    }

    isListening = true;
    root = DefaultRootWindow(dpy);
    
    XEvent ev;
    
    while(true){
        XNextEvent(dpy, &ev);
        for(int i = 0; i < keys.size(); i++){
            if(ev.type == KeyPress && ev.xkey.keycode == keys.at(i).key && ev.xkey.state == keys.at(i).mask){
                _env->CallStaticVoidMethod(cls, mid, keys.at(i).id);
                break;
            }
        }
    }
    
    return;
}
