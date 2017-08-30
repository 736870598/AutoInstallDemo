package com.sxy.autoinstalldemo;

import android.accessibilityservice.AccessibilityService;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import java.util.HashMap;

/**
 * 辅助服务service
 * Created by sunxiaoyu on 2017/8/29.
 */
public class ApkService extends AccessibilityService {

    HashMap<Integer, Boolean> hashMap = new HashMap();
    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {

        AccessibilityNodeInfo nodeInfo = event.getSource();
        if (nodeInfo != null){
            boolean hander = interNoderInfo(nodeInfo);
            if (hander){
                hashMap.put(event.getWindowId(), true);
            }
        }
    }

    private boolean interNoderInfo(AccessibilityNodeInfo nodeInfo) {
        if (nodeInfo.getClassName().equals("android.widget.Button")){
            String nodeContent = nodeInfo.getText().toString();
            if (nodeContent.equals("确定")||nodeContent.equals("完成")||nodeContent.equals("安装")){
                nodeInfo.performAction(AccessibilityNodeInfo.ACTION_CLICK);
            }
        }else if(nodeInfo.getClassName().equals("android.widget.ScrollView")){
            nodeInfo.performAction(AccessibilityNodeInfo.ACTION_SCROLL_FORWARD);
        }

        int childCount = nodeInfo.getChildCount();
        for (int i = 0; i < childCount; i++){
            AccessibilityNodeInfo nodeInfos = nodeInfo.getChild(i);
            if (nodeInfos != null){
                if (interNoderInfo(nodeInfos)){
                    return true;
                }
            }
        }

        return false;
    }


    @Override
    public void onInterrupt() {

    }
}
