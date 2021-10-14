/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package com.erhannis.painlessmeshjava;

import com.erhannis.mathnstuff.components.OptionsFrame;
import com.erhannis.mathnstuff.utils.DThread;
import com.erhannis.mathnstuff.utils.Options;
import com.erhannis.painlessmeshjava.ConnectivityManager.NetworkInfo;
import com.erhannis.painlessmeshjava.WifiManager.WifiConfiguration;
import com.erhannis.painlessmeshjava.WifiManager.WifiInfo;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JScrollBar;
import javax.swing.SwingUtilities;
import org.joda.time.DateTime;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * This whole project translated from https://gitlab.com/painlessMesh/painlessmesh_android
 * 
 * @author erhannis
 */
public class MeshFrame extends javax.swing.JFrame {

    /**
     * Tag for debug messages of service
     */
    private static final String DBG_TAG = "MeshActivity";

    /**
     * Flag if we try to connect to Mesh
     */
    private static boolean tryToConnect = false;
    /**
     * Flag if connection to Mesh was started
     */
    private static boolean isConnected = false;
    /**
     * Flag when user stops connection
     */
    private static boolean userDisConRequest = false;

    /**
     * WiFi manager to connect to Mesh network
     */
    private WifiManager wifiMgr;

    /**
     * Mesh name == Mesh SSID
     */
    private String meshName;
    /**
     * Mesh password == Mesh network password
     */
    private String meshPw;
    /**
     * Mesh port == TCP port number
     */
    private static int meshPort;

    /**
     * WiFi AP to which device was connected before connecting to mesh
     */
    private String oldAPName = "";
    /**
     * Mesh network entry IP
     */
    private static String meshIP;

    /**
     * My Mesh node id
     */
    static long myNodeId = 0;
    /**
     * The node id we connected to
     */
    static long apNodeId = 0;

    /**
     * Filter for incoming messages
     */
    private long filterId = 0;

    /**
     * Predefined message 1
     */
    private String predMsg1;
    /**
     * Predefined message 2
     */
    private String predMsg2;
    /**
     * Predefined message 3
     */
    private String predMsg3;
    /**
     * Predefined message 4
     */
    private String predMsg4;
    /**
     * Predefined message 5
     */
    private String predMsg5;

    /**
     * Flag if log file should be written
     */
    private static boolean doLogging = true;
    /**
     * For log file of data
     */
    static BufferedWriter out = null; //TODO Make not static?
    /**
     * Path to storage folder
     */
    private static String sdcardPath;
    /**
     * Log file URI
     */
    private String logFilePath;
    
    private final Options options;

    public void onResume() {
        // Get previous mesh network credentials
        meshName = (String) options.getOrDefault("pm_ssid", "prefs_name_hint");
        meshPw = (String) options.getOrDefault("pm_pw", "prefs_pw_hint");
        meshPort = (Integer) options.getOrDefault("pm_port", 5555);

        // Get predefined messages
        predMsg1 = (String) options.getOrDefault("msg_1", "");
        predMsg2 = (String) options.getOrDefault("msg_2", "");
        predMsg3 = (String) options.getOrDefault("msg_3", "");
        predMsg4 = (String) options.getOrDefault("msg_4", "");
        predMsg5 = (String) options.getOrDefault("msg_5", "");

        // Get path to SDCard
        sdcardPath = System.getProperty("user.home") + "/.painlessMesh/";

        // Get the wifi manager
        wifiMgr = new WifiManager();

        // View for filter button
        JButton ib_to_set = bt_filter;
        // Set onClickListener
        ib_to_set.addActionListener(v12 -> handleFilterRequest());
        // View for clean button
        ib_to_set = bt_clean;
        // Set onClickListener
        ib_to_set.addActionListener(v12 -> mesh_msgs.setText(""));
//        // View for share button
//        ib_to_set = bt_share;
//        // Set onClickListener
//        ib_to_set.addActionListener(v12 -> handleShareRequest());

        // View for predefined message 1 button
        JButton bt_to_set = bt_bc_pred_msg_1;
        // Set onClickListener
        bt_to_set.addActionListener(v12 -> {
            if (MeshCommunicator.isConnected()) {
                MeshHandler.sendNodeMessage(0, predMsg1);
            }
        });
        // View for predefined message 2 button
        bt_to_set = bt_bc_pred_msg_2;
        // Set onClickListener
        bt_to_set.addActionListener(v12 -> {
            if (MeshCommunicator.isConnected()) {
                MeshHandler.sendNodeMessage(0, predMsg2);
            }
        });
        // View for predefined message 2 button
        bt_to_set = bt_bc_pred_msg_3;
        // Set onClickListener
        bt_to_set.addActionListener(v12 -> {
            if (MeshCommunicator.isConnected()) {
                MeshHandler.sendNodeMessage(0, predMsg3);
            }
        });
        // View for predefined message 2 button
        bt_to_set = bt_bc_pred_msg_4;
        // Set onClickListener
        bt_to_set.addActionListener(v12 -> {
            if (MeshCommunicator.isConnected()) {
                MeshHandler.sendNodeMessage(0, predMsg4);
            }
        });
        // View for predefined message 2 button
        bt_to_set = bt_bc_pred_msg_5;
        // Set onClickListener
        bt_to_set.addActionListener(v12 -> {
            if (MeshCommunicator.isConnected()) {
                MeshHandler.sendNodeMessage(0, predMsg5);
            }
        });
        // View for time sync request button
        bt_to_set = bt_time_sync;
        // Set onClickListener
        bt_to_set.addActionListener(v12 -> {
            if (MeshCommunicator.isConnected()) {
                MeshHandler.sendTimeSyncRequest();
            }
        });
        // View for node sync request button
        bt_to_set = bt_node_sync;
        // Set onClickListener
        bt_to_set.addActionListener(v12 -> {
            if (MeshCommunicator.isConnected()) {
                MeshHandler.sendNodeSyncRequest();
            }
        });
        
        mi_mesh_conn_bt.addActionListener(v -> {
            handleConnection();
        });

        // Register Mesh events
        ArrayList<String> intentFilter = new ArrayList<>();
        intentFilter.add(MeshCommunicator.MESH_DATA_RECVD);
        intentFilter.add(MeshCommunicator.MESH_SOCKET_ERR);
        intentFilter.add(MeshCommunicator.MESH_CONNECTED);
        intentFilter.add(MeshCommunicator.MESH_NODES);
        intentFilter.add(MeshCommunicator.MESH_OTA);
        intentFilter.add(MeshCommunicator.MESH_OTA_REQ);
        // Register network change events
        intentFilter.add(WifiManager.CONNECTIVITY_ACTION); // "android.net.conn.CONNECTIVITY_CHANGE"
        // Register receiver
        IntentManager.SINGLETON.registerReceiver(localBroadcastReceiver, intentFilter);
    }

    protected void onDestroy() {
        if (MeshCommunicator.isConnected()) {
            MeshCommunicator.Disconnect();
        }
        stopLogging();
        // unregister the broadcast receiver
        IntentManager.SINGLETON.unregisterReceiver(localBroadcastReceiver);
    }

    /**
     * Apply or remove filter to show only messages from a specific node
     */
    private void handleFilterRequest() {
        if (MeshCommunicator.isConnected()) {
            ArrayList<String> nodesListStr = new ArrayList<>();

            ArrayList<Long> tempNodesList = new ArrayList<>(MeshHandler.nodesList);

            tempNodesList.add(0L);
            Collections.sort(tempNodesList);

            for (int idx = 0; idx < tempNodesList.size(); idx++) {
                nodesListStr.add(String.valueOf(tempNodesList.get(idx)));
            }
            nodesListStr.set(0, "mesh_filter_clear");

            filterId = tempNodesList.get(nodesListStr.indexOf(JOptionPane.showInputDialog(null, "Filter body", "Filter title", JOptionPane.PLAIN_MESSAGE, null, nodesListStr.toArray(), 0)));
        } else {
            showToast("mesh_no_connection");
        }
    }

    /**
     * Handle connect action events. Depending on current status - Start
     * connection request - Cancel connection request if pending - Stop
     * connection to the mesh network
     */
    private void handleConnection() {
        if (!isConnected) {
            if (tryToConnect) {
                stopConnection();
            } else {
                startConnectionRequest();
            }
        } else {
            stopConnection();
        }
    }

    /**
     * Add mesh network AP to the devices list of Wifi APs Enable mesh network
     * AP to initiate connection to the mesh AP
     */
    private void startConnectionRequest() {
        mi_mesh_conn_bt.setText("Disconnect");
        tryToConnect = true;
        userDisConRequest = false;

        tv_mesh_conn_status.setText("mesh_connecting");

        // Get current active WiFi AP
        oldAPName = "";

        // Get current WiFi connection
        ConnectivityManager connManager = ConnectivityManager.SINGLETON;
        if (connManager != null) {
            NetworkInfo networkInfo = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            if (networkInfo.isConnected()) {
                final WifiInfo connectionInfo = wifiMgr.getConnectionInfo();
                if (connectionInfo != null && !connectionInfo.getSSID().isEmpty()) {
                    oldAPName = connectionInfo.getSSID();
                }
            }
        }

        // Add device AP to network list and enable it
        WifiConfiguration meshAPConfig = new WifiConfiguration();
        meshAPConfig.SSID = "\"" + meshName + "\"";
        meshAPConfig.preSharedKey = "\"" + meshPw + "\"";
        int newId = wifiMgr.addNetwork(meshAPConfig);
        if (BuildConfig.DEBUG) {
            System.out.println(DBG_TAG + " Result of addNetwork: " + newId);
        }
        wifiMgr.disconnect();
        wifiMgr.enableNetwork(newId, true);
        wifiMgr.reconnect();
    }

    /**
     * Stop connection to the mesh network Disable the mesh AP in the device
     * Wifi AP list so that the device reconnects to its default AP
     */
    private void stopConnection() {
        mi_mesh_conn_bt.setText("Connect");

        if (MeshCommunicator.isConnected()) {
            MeshCommunicator.Disconnect();
        }
        isConnected = false;
        tryToConnect = false;
        userDisConRequest = true;
        
        
//        List<WifiConfiguration> availAPs = wifiMgr.getConfiguredNetworks();
//
//        if (oldAPName.isEmpty()) {
//            for (int index = 0; index < availAPs.size(); index++) {
//                if (availAPs.get(index).SSID.equalsIgnoreCase("\"" + meshName + "\"")) {
//                    wifiMgr.disconnect();
//                    wifiMgr.disableNetwork(availAPs.get(index).networkId);
//                    if (BuildConfig.DEBUG) {
//                        System.out.println(DBG_TAG + " Disabled: " + availAPs.get(index).SSID);
//                    }
//                    wifiMgr.reconnect();
//                    break;
//                }
//            }
//        } else {
//            for (int index = 0; index < availAPs.size(); index++) {
//                if (availAPs.get(index).SSID.equalsIgnoreCase(oldAPName)) {
//                    wifiMgr.disconnect();
//                    wifiMgr.enableNetwork(availAPs.get(index).networkId, true);
//                    if (BuildConfig.DEBUG) {
//                        System.out.println(DBG_TAG + " Re-enabled: " + availAPs.get(index).SSID);
//                    }
//                    wifiMgr.reconnect();
//                    break;
//                }
//            }
//        }
        wifiMgr.disconnect();

        tv_mesh_conn_status.setText("mesh_disconnected");
        stopLogging();
    }

    /**
     * Show known mesh nodes as preparation of sending a message Uses a
     * temporary nodes list in case the nodes list is refreshed while this
     * dialog is still open Adds a BROADCAST node to enable sending broadcast
     * messages to the mesh network
     */
    private void selectNodesForSending() {
        ArrayList<String> nodesListStr = new ArrayList<>();

        ArrayList<Long> tempNodesList = new ArrayList<>(MeshHandler.nodesList);

        tempNodesList.add(0L);
        Collections.sort(tempNodesList);

        for (int idx = 0; idx < tempNodesList.size(); idx++) {
            nodesListStr.add(String.valueOf(tempNodesList.get(idx)));
        }
        nodesListStr.set(0, "mesh_send_broadcast");

        showSendDialog(tempNodesList.get(nodesListStr.indexOf(JOptionPane.showInputDialog(null, "Filter body", "Filter title", JOptionPane.PLAIN_MESSAGE, null, nodesListStr.toArray(), 0))));
    }

    /**
     * Show the dialog to send a message to the mesh network Options - Send a
     * time sync request - Send a node sync request - Send a user message
     *
     * @param selectedNode nodeID that the message should be sent to
     */
    private void showSendDialog(long selectedNode) {
        SendFrame sf = new SendFrame();

        if (selectedNode == 0) {
            sf.setTitle("Broadcast");
        } else {
            sf.setTitle("Send to: " + selectedNode);
        }

        final long rcvNodeId = selectedNode;
        sf.btnSend.addActionListener(v -> {
            MeshHandler.sendNodeMessage(rcvNodeId, sf.taMessage.getText());
        });

        // Set the button functions
        sf.btnMsg1.addActionListener(v12 -> sf.taMessage.setText(predMsg1));
        sf.btnMsg2.addActionListener(v12 -> sf.taMessage.setText(predMsg2));
        sf.btnMsg3.addActionListener(v12 -> sf.taMessage.setText(predMsg3));
        sf.btnMsg4.addActionListener(v12 -> sf.taMessage.setText(predMsg4));
        sf.btnMsg5.addActionListener(v12 -> sf.taMessage.setText(predMsg5));
        
        sf.setVisible(true);
    }

    /**
     * Show dialog to select OTA file and node type and initiate advertise of
     * OTA
     */
    private void showOtaInfoDialog() {
//        // Open dialog box to enter required info and then advertise the OTA
//        // Get dialog layout
//        LayoutInflater li = LayoutInflater.from(this);
//        @SuppressLint("InflateParams")
//        View otaPrepareView = li.inflate(R.layout.ota_advertise, null);
//
//        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
//                this);
//
//        tvOtaFile = otaPrepareView.findViewById(R.id.tv_ota_file);
//        tvOtaFile.setText("");
//
//        tvOtaMd5 = otaPrepareView.findViewById(R.id.tv_ota_md5);
//        tvOtaMd5.setText("");
//
//        final EditText etNodeType = otaPrepareView.findViewById(R.id.et_node_type);
//
//        final RadioGroup rgHwSelection = otaPrepareView.findViewById(R.id.rg_hardware);
//        final RadioButton rbForceUpdate = otaPrepareView.findViewById(R.id.rb_force);
//
//        final Button selFileButton = otaPrepareView.findViewById(R.id.bt_select_file);
//
//        // set prompts.xml to alert dialog builder
//        alertDialogBuilder.setView(otaPrepareView)
//                .setNegativeButton(getString(android.R.string.cancel),
//                        (dialog, which) -> {
//                            // Do something here if you want
//                            dialog.dismiss();
//                        })
//                .setPositiveButton("ota_advertise",
//                        (dialog, which) -> {
//                            String otaFileName = tvOtaFile.getText().toString();
//                            if (otaFileName.isEmpty()) {
//                                showToast("ota_miss_file");
//                                return;
//                            }
//                            String nodeType = etNodeType.getText().toString();
//                            if (nodeType.isEmpty()) {
//                                showToast("ota_miss_type");
//                                return;
//                            }
//                            int hwType = (rgHwSelection.getCheckedRadioButtonId() == R.id.rb_esp32) ? 0 : 1;
//                            boolean forcedUpdate = rbForceUpdate.isChecked();
//                            MeshHandler.sendOTAAdvertise(hwType, nodeType, forcedUpdate);
//                            // Do something here if you want
//                            dialog.dismiss();
//                        });
//        // create alert dialog
//        final AlertDialog alertDialog = alertDialogBuilder.create();
//
//        // show it
//        alertDialog.show();
//
//        selFileButton.setOnClickListener(v12 -> openFileChooser());
    }

    /**
     * Scroll the text view with the received messages to the bottom
     */
    private void scrollViewDown() {
        JScrollBar vertical = jScrollPane1.getVerticalScrollBar();
        vertical.setValue( vertical.getMaximum() );
    }

    /**
     * Show a custom toast (different colors, located in the center of the
     * screen
     *
     * @param msg Text to be displayed in the toast
     */
    private void showToast(String msg) {
        System.out.println("TOAST " + msg); //TODO Fancier?
    }

    
    private JFileChooser fileChooser = new JFileChooser();
    
    /**
     * Open document handler to choose update file
     */
//    private void openFileChooser() {
//        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
//            File otaFile = fileChooser.getSelectedFile();
//
//            
//            if (otaFile != null) {
//                MeshHandler.otaPath = otaFile.getAbsolutePath();
//                MeshHandler.otaFile = otaFile;
//                MeshHandler.otaMD5 = MeshHandler.calculateMD5(MeshHandler.otaFile);
//                MeshHandler.otaFileSize = MeshHandler.otaFile.length();
//                SwingUtilities.invokeLater(() -> {
//                    //??? Handler - there was a nested handler.post here???
//                    tvOtaFile.setText(MeshHandler.otaPath.substring(MeshHandler.otaPath.lastIndexOf("/") + 1));
//                    tvOtaMd5.setText(MeshHandler.otaMD5);
//                });
//            }
//        }
//    }

    /**
     * Start logging the received messages TODO double check this works for all
     * Android versions
     */
    private void startLogging() {
        if (doLogging) {
            stopLogging();
        }

        DateTime now = new DateTime();
        String logTitle = String.format("Log created: %02d/%02d/%02d %02d:%02d\n\n", now.getYear() - 2000, now.getMonthOfYear(),
                now.getDayOfMonth(), now.getHourOfDay(), now.getMinuteOfHour());
        /* Name of the log */
        String logName = "meshLogFile.txt";

        // Create folder for this data set
        try {
            File appDir = new File(sdcardPath);
            boolean exists = appDir.exists();
            if (!exists) {
                boolean result = appDir.mkdirs();
                if (!result) {
                    System.out.println(DBG_TAG + " Failed to create log folder");
                }
            }
        } catch (Exception exc) {
            System.err.println(DBG_TAG + " Failed to create log folder: " + exc);
        }

        // If file is still open for writing, close it first
        if (out != null) {
            try {
                out.flush();
                out.close();
            } catch (IOException exc) {
                System.err.println(DBG_TAG + " Failed to close log file: " + exc);
            }
        }

        // TODO find a better solution to handle the log files
        // For now delete the old log file to avoid getting a too large file
        boolean result = new File(sdcardPath + logName).exists();
        if (result) {
            result = new File(sdcardPath + logName).delete();
            if (!result) {
                System.out.println(DBG_TAG + " Failed to delete the old logfile");
            }
        }

        try {
            logFilePath = sdcardPath + logName;
            FileWriter newFile = new FileWriter(logFilePath);
            out = new BufferedWriter(newFile);
            out.append(logTitle);
        } catch (IOException exc) {
            System.err.println(DBG_TAG + " Failed to open log file for writing: " + exc);
        }

        doLogging = true;
    }

    /**
     * Stop logging of the received messages
     */
    private void stopLogging() {
        if (doLogging) {
            if (out != null) {
                try {
                    out.flush();
                    out.close();
                } catch (IOException exc) {
                    System.err.println(DBG_TAG + " Failed to close log file: " + exc);
                }
                out = null;
            }
            doLogging = false;
            String[] toBeScannedStr = new String[1];
            toBeScannedStr[0] = sdcardPath + "*";
            // Pretty sure this is not applicable here
            //MediaScannerConnection.scanFile(this, toBeScannedStr, null, (path, uri) -> System.out.println("SCAN COMPLETED: " + path));
        }
    }

    /**
     * Local broadcast receiver Registered for - WiFi connection change events -
     * Mesh network data events - Mesh network error events
     */
    private final BiConsumer<String, String> localBroadcastReceiver = new BiConsumer<String, String>() {
        @Override
        public void accept(String intentAction, String msg) {
            // Connection change
            System.out.println(DBG_TAG + " Received broadcast: " + intentAction);
            // WiFi events
            if (isConnected) {
                // Did we loose connection to the mesh network?
                /* Access to connectivity manager */
                ConnectivityManager cm = ConnectivityManager.SINGLETON;
                /* WiFi connection information  */
                NetworkInfo wifiOn;
                if (cm != null) {
                    wifiOn = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
                    if (!wifiOn.isConnected()) {
                        isConnected = false;
                        SwingUtilities.invokeLater(() -> stopConnection());
                    }
                }
            }
            if (tryToConnect && (intentAction != null) && (intentAction.equals(WifiManager.CONNECTIVITY_ACTION))) {
                /* Access to connectivity manager */
                ConnectivityManager cm = ConnectivityManager.SINGLETON;
                /* WiFi connection information  */
                NetworkInfo wifiOn;
                if (cm != null) {
                    wifiOn = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
                    if (wifiOn.isConnected()) {
                        if (tryToConnect) {
                            WifiInfo wifiInfo = wifiMgr.getConnectionInfo();
                            String ssid = null;
                            try {
                                ssid = wifiInfo.getSSID();
                                System.out.println("ssid: " + ssid);
                            } catch (Throwable t) {
                                t.printStackTrace();
                            }
                            if (ssid.equalsIgnoreCase(meshName)) {
                                System.out.println(DBG_TAG + " Connected to Mesh network wifiOn.getExtraInfo()");
                                // Get the gateway IP address
                                if (wifiMgr != null) {
                                    // Create the mesh AP node ID from the AP MAC address
                                    apNodeId = MeshHandler.createMeshID(wifiInfo.getBSSID());

                                    meshIP  = wifiInfo.getProbableGateway();

                                    // Create our node ID
                                    myNodeId = MeshHandler.createMeshID(MeshHandler.getWifiMACAddress());
                                } else {
                                    // We are screwed. Tell user about the problem
                                    System.err.println(DBG_TAG + " Critical Error -- cannot get WifiManager access");
                                }
                                // Rest has to be done on UI thread
                                SwingUtilities.invokeLater(() -> {
                                    tryToConnect = false;

                                    String connMsg = "ID: " + myNodeId + " on " + meshName;
                                    tv_mesh_conn_status.setText(connMsg);

                                    // Set flag that we are connected
                                    isConnected = true;

                                    startLogging();

                                    // Connected to the Mesh network, start network task now
                                    MeshCommunicator.Connect(meshIP, meshPort);

                                });
                            } else {
                                tv_mesh_conn_status.setText("Not connected to right wifi");
//                                List<WifiConfiguration> availAPs = wifiMgr.getConfiguredNetworks();
//
//                                for (int index = 0; index < availAPs.size(); index++) {
//                                    if (availAPs.get(index).SSID.equalsIgnoreCase("\"" + meshName + "\"")) {
//                                        wifiMgr.disconnect();
//                                        wifiMgr.enableNetwork(availAPs.get(index).networkId, true);
//                                        if (BuildConfig.DEBUG) {
//                                            System.out.println(DBG_TAG + " Retry to enable: " + availAPs.get(index).SSID);
//                                        }
//                                        wifiMgr.reconnect();
//                                        break;
//                                    }
//                                }
                            }
                        }
                    }
                }
            }

            String dataSet;
            DateTime now = new DateTime();
            dataSet = String.format("[%02d:%02d:%02d:%03d] ",
                    now.getHourOfDay(),
                    now.getMinuteOfHour(),
                    now.getSecondOfMinute(),
                    now.getMillisOfSecond());

            // Mesh events
            if (MeshCommunicator.MESH_DATA_RECVD.equals(intentAction)) {
                String rcvdMsg = msg;
                String oldText;
                try {
                    JSONObject rcvdJSON = new JSONObject(rcvdMsg);
                    int msgType = rcvdJSON.getInt("type");
                    long fromNode = rcvdJSON.getLong("from");
                    switch (msgType) {
                        case 3: // TIME_DELAY
                            tv_mesh_last_event.setText("mesh_event_time_delay");
                            dataSet += "Received TIME_DELAY\n";
                            break;
                        case 4: // TIME_SYNC
                            tv_mesh_last_event.setText("mesh_event_time_sync");
                            dataSet += "Received TIME_SYNC\n";
                            break;
                        case 5: // NODE_SYNC_REQUEST
                        case 6: // NODE_SYNC_REPY
                            if (msgType != 5) {
                                tv_mesh_last_event.setText("mesh_event_node_reply");
                                dataSet += "Received NODE_SYNC_REPLY\n";
                            } else {
                                tv_mesh_last_event.setText("mesh_event_node_req");
                                dataSet += "Received NODE_SYNC_REQUEST\n";
                            }
                            // Generate known nodes list
                            final String nodesListString = rcvdMsg;
                            //??? Handler
                            new DThread(() -> MeshHandler.generateNodeList(nodesListString)).start();
                            break;
                        case 7: // CONTROL ==> deprecated
                            dataSet += "Received CONTROL\n";
                            break;
                        case 8: // BROADCAST
                            dataSet += "Broadcast:\n" + rcvdJSON.getString("msg") + "\n";
                            if (filterId != 0) {
                                if (fromNode != filterId) {
                                    return;
                                }
                            }
                            oldText = "BC from " + fromNode + "\n\t" + rcvdJSON.getString("msg") + "\n";
                            mesh_msgs.append(oldText);
                            break;
                        case 9: // SINGLE
                            dataSet += "Single Msg:\n" + rcvdJSON.getString("msg") + "\n";
                            // Check if the message is a OTA req message
                            JSONObject rcvdData = new JSONObject(rcvdJSON.getString("msg"));
                            String dataType = rcvdData.getString("plugin");
                            if ((dataType != null) && dataType.equalsIgnoreCase("ota")) {
                                dataType = rcvdData.getString("type");
                                if (dataType != null) {
                                    if (dataType.equalsIgnoreCase("version")) {
                                        // We received a OTA advertisment!
                                        tv_mesh_last_event.setText("mesh_event_ota_adv");
                                        return;
                                    } else if (dataType.equalsIgnoreCase("request")) {
                                        // We received a OTA block request
                                        MeshHandler.sendOtaBlock(fromNode, rcvdData.getLong("partNo"));
                                        tv_mesh_last_event.setText("mesh_event_ota_req");
                                    }
                                }
                            }
                            if (filterId != 0) {
                                if (fromNode != filterId) {
                                    return;
                                }
                            }
                            oldText = "SM from " + fromNode + "\n\t" + rcvdJSON.getString("msg") + "\n";
                            mesh_msgs.append(oldText);
                            break;
                    }
                } catch (JSONException e) {
                    System.out.println(DBG_TAG + " Received message is not a JSON Object!");
                    oldText = "E: " + msg + "\n";
                    mesh_msgs.append(oldText);
                    dataSet += "ERROR INVALID DATA:\n" + msg + "\n";
                }
                if (out != null) {
                    try {
                        out.append(dataSet);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                scrollViewDown();
            } else if (MeshCommunicator.MESH_SOCKET_ERR.equals(intentAction)) {
                if (MeshHandler.nodesList != null) {
                    MeshHandler.nodesList.clear();
                }
                if (!userDisConRequest) {
                    showToast("mesh_lost_connection");
                    MeshCommunicator.Connect(meshIP, meshPort);
                    tv_mesh_last_event.setText(msg);
                }
            } else if (MeshCommunicator.MESH_CONNECTED.equals(intentAction)) {
                userDisConRequest = false;
            } else if (MeshCommunicator.MESH_NODES.equals(intentAction)) {
                String oldText = msg + "\n";
                mesh_msgs.append(oldText);
                scrollViewDown();
                dataSet += msg + "\n";
                if (out != null) {
                    try {
                        out.append(dataSet);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            String oldText = mesh_msgs.getText().toString();
            // Check if the text is getting too long
            if (oldText.length() > 16535) {
                // Quite long, remove the first 20 lines  from the text
                int indexOfCr = 0;
                for (int lines = 0; lines < 20; lines++) {
                    indexOfCr = oldText.indexOf("\n", indexOfCr + 1);
                }
                oldText = oldText.substring(indexOfCr + 1);
                mesh_msgs.setText(oldText);
            }
        }
    };

    /**
     * Creates new form MeshFrame
     */
    public MeshFrame() {
        initComponents();

        this.options = Options.demandOptions("options.dat");
        
        // Start handler to send node sync request and time sync request every 10 seconds
        // Keeps socket connection more stable
        //??? Handler
        new DThread(() -> {
            try {
                boolean timeForNodeReq = true;
                while (true) {
                    Thread.sleep(10000);
                    if (MeshCommunicator.isConnected()) {
                        if (timeForNodeReq) {
                            MeshHandler.sendNodeSyncRequest();
                            timeForNodeReq = false;
                        } else {
                            MeshHandler.sendTimeSyncRequest();
                            timeForNodeReq = true;
                        }
                    }
                }
            } catch (InterruptedException ex) {
                Logger.getLogger(MeshFrame.class.getName()).log(Level.SEVERE, null, ex);
            }
        }).start();

        onResume();
        
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onDestroy();
                System.exit(0);
            }
        });
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        tv_mesh_conn_status = new javax.swing.JLabel();
        tv_mesh_last_event = new javax.swing.JLabel();
        bt_clean = new javax.swing.JButton();
        bt_filter = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        mesh_msgs = new javax.swing.JTextArea();
        bt_bc_pred_msg_1 = new javax.swing.JButton();
        bt_bc_pred_msg_2 = new javax.swing.JButton();
        bt_bc_pred_msg_3 = new javax.swing.JButton();
        bt_bc_pred_msg_4 = new javax.swing.JButton();
        bt_bc_pred_msg_5 = new javax.swing.JButton();
        bt_node_sync = new javax.swing.JButton();
        bt_time_sync = new javax.swing.JButton();
        mi_mesh_conn_bt = new javax.swing.JButton();
        bt_send = new javax.swing.JButton();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        jMenuItem1 = new javax.swing.JMenuItem();
        jMenuItem2 = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);

        tv_mesh_conn_status.setText("ID: ####");

        tv_mesh_last_event.setText("####");

        bt_clean.setText("Clear");

        bt_filter.setText("Filter");

        mesh_msgs.setColumns(20);
        mesh_msgs.setRows(5);
        jScrollPane1.setViewportView(mesh_msgs);

        bt_bc_pred_msg_1.setText("1");

        bt_bc_pred_msg_2.setText("2");

        bt_bc_pred_msg_3.setText("3");

        bt_bc_pred_msg_4.setText("4");

        bt_bc_pred_msg_5.setText("5");

        bt_node_sync.setText("NSR");

        bt_time_sync.setText("TSR");

        mi_mesh_conn_bt.setText("Connect");

        bt_send.setText("Send...");
        bt_send.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bt_sendActionPerformed(evt);
            }
        });

        jMenu1.setText("Windows");

        jMenuItem1.setText("Options...");
        jMenuItem1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem1ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem1);

        jMenuItem2.setText("OTA...");
        jMenuItem2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jMenuItem2ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem2);

        jMenuBar1.add(jMenu1);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(tv_mesh_conn_status)
                            .addComponent(tv_mesh_last_event))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(mi_mesh_conn_bt)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(bt_send)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(bt_clean)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(bt_filter))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(bt_bc_pred_msg_1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(bt_bc_pred_msg_2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(bt_bc_pred_msg_3)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(bt_bc_pred_msg_4)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(bt_bc_pred_msg_5)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(bt_node_sync)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(bt_time_sync)
                        .addGap(0, 226, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(tv_mesh_conn_status)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(tv_mesh_last_event))
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(bt_filter)
                        .addComponent(bt_clean)
                        .addComponent(mi_mesh_conn_bt)
                        .addComponent(bt_send)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 167, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(bt_bc_pred_msg_1)
                    .addComponent(bt_bc_pred_msg_2)
                    .addComponent(bt_bc_pred_msg_3)
                    .addComponent(bt_bc_pred_msg_4)
                    .addComponent(bt_bc_pred_msg_5)
                    .addComponent(bt_node_sync)
                    .addComponent(bt_time_sync))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jMenuItem1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem1ActionPerformed
        new OptionsFrame(options).setVisible(true);
    }//GEN-LAST:event_jMenuItem1ActionPerformed

    private void bt_sendActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bt_sendActionPerformed
        if (MeshCommunicator.isConnected()) {
            selectNodesForSending();
        } else {
            showToast("mesh_no_connection");
        }
    }//GEN-LAST:event_bt_sendActionPerformed

    private void jMenuItem2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jMenuItem2ActionPerformed
        showOtaInfoDialog();
    }//GEN-LAST:event_jMenuItem2ActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(MeshFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(MeshFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(MeshFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(MeshFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new MeshFrame().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton bt_bc_pred_msg_1;
    private javax.swing.JButton bt_bc_pred_msg_2;
    private javax.swing.JButton bt_bc_pred_msg_3;
    private javax.swing.JButton bt_bc_pred_msg_4;
    private javax.swing.JButton bt_bc_pred_msg_5;
    private javax.swing.JButton bt_clean;
    private javax.swing.JButton bt_filter;
    private javax.swing.JButton bt_node_sync;
    private javax.swing.JButton bt_send;
    private javax.swing.JButton bt_time_sync;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem jMenuItem1;
    private javax.swing.JMenuItem jMenuItem2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextArea mesh_msgs;
    private javax.swing.JButton mi_mesh_conn_bt;
    private javax.swing.JLabel tv_mesh_conn_status;
    private javax.swing.JLabel tv_mesh_last_event;
    // End of variables declaration//GEN-END:variables
}
