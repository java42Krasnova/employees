package telran.net;

import java.io.Serializable;

public interface Sender {
<T> T send(String requestType, Serializable requestData);
}
