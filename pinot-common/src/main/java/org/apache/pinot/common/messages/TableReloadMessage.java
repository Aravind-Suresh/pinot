package org.apache.pinot.common.messages;

import java.util.UUID;
import org.apache.helix.model.Message;
import org.apache.helix.zookeeper.datamodel.ZNRecord;

/**
 * This (Helix) message is sent from the controller to brokers when a request is received to reload the table.
 *
 * NOTE: We keep the table name as a separate key instead of using the Helix PARTITION_NAME so that this message can be
 *       used for any resource.
 */
public class TableReloadMessage extends Message {
    public static final String RELOAD_TABLE_MSG_SUB_TYPE = "RELOAD_TABLE";

    private static final String TABLE_NAME_KEY = "tableName";

    /**
     * Constructor for the sender.
     */
    public TableReloadMessage(String tableNameWithType) {
        super(MessageType.USER_DEFINE_MSG, UUID.randomUUID().toString());
        setMsgSubType(RELOAD_TABLE_MSG_SUB_TYPE);
        // Give it infinite time to process the message, as long as session is alive
        setExecutionTimeout(-1);
        // Set the Pinot specific fields
        // NOTE: DO NOT use Helix field "PARTITION_NAME" because it can be overridden by Helix while sending the message
        ZNRecord znRecord = getRecord();
        znRecord.setSimpleField(TABLE_NAME_KEY, tableNameWithType);
    }

    /**
     * Constructor for the receiver.
     */
    public TableReloadMessage(Message message) {
        super(message.getRecord());
        if (!message.getMsgSubType().equals(RELOAD_TABLE_MSG_SUB_TYPE)) {
            throw new IllegalArgumentException("Invalid message subtype:" + message.getMsgSubType());
        }
    }

    public String getTableNameWithType() {
        return getRecord().getSimpleField(TABLE_NAME_KEY);
    }
}
