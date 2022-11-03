# Pisano Feedback Sample Application

It is a sample android application using the Feedback SDK.

![Main Image](https://media.giphy.com/media/uJUaQAwMhgGIk944X5/giphy.gif)

## How to use Feedback SDK

You can check the latest releases [here](https://central.sonatype.dev/artifact/co.pisano.feedback/feedback/1.2.17).

### Supported Android Versions
Pisano for Android supports Android API 16 and above.

### Installation
The repository typically goes into the build.gradle file in the root of your project:

```yaml
allprojects { 
    repositories { 
      ... 
      mavenCentral() 
    } 
}
```

Add the Pisano SDK dependency to the app/build.gradle file. 

```yaml
dependencies {
    implementation 'co.pisano.feedback:feedback:1.2.17'
}
```
### Permissions

In order to use Pisano Android SDK, you should include the following permissions in  AndroidManifest.xml 

```yaml
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
```

If you would like your customers to upload a file together with their feedback, you should also include the following permissions. 

```yaml
<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
```

## Usage SDK 
### Initialize Method
#### Kotlin

```yaml
import co.pisano.feedback.data.helper.ActionListener
import co.pisano.feedback.data.helper.PisanoActions
import co.pisano.feedback.managers.PisanoSDK
import co.pisano.feedback.managers.PisanoSDKManager

val manager = PisanoSDKManager.Builder(context)
    .setApplicationId("")
    .setAccessKey("")
    .setApiUrl("")
    .setFeedbackUrl("")
    .setEventUrl("")
    .setCloseStatusCallback(object : ActionListener {
        override fun action(action: PisanoActions) {
            when (action) {
                PisanoActions.CLOSED -> {}
                PisanoActions.OPENED -> {}
                PisanoActions.OUTSIDE -> {}
                PisanoActions.SEND_FEEDBACK -> {}
                PisanoActions.DISPLAY_ONCE -> {}
                PisanoActions.PREVENT_MULTIPLE_FEEDBACK -> {}
                else -> {}
            }

            Log.d("ActionState", "action: $action")
        }
    })
    .build()

PisanoSDK.init(manager)
```

#### Java

```yaml
import co.pisano.feedback.data.helper.ActionListener;
import co.pisano.feedback.data.helper.PisanoActions;
import co.pisano.feedback.managers.PisanoSDK;
import co.pisano.feedback.managers.PisanoSDKManager;

PisanoSDKManager pisanoSDKManager = new PisanoSDKManager.Builder(this)
        .setApplicationId("")
        .setAccessKey("")
        .setApiUrl("")
        .setFeedbackUrl("")
        .setEventUrl("")
        .setCloseStatusCallback(new ActionListener() {
            @Override
            public void action(@NonNull PisanoActions pisanoActions) {
                switch (pisanoActions) {
                    case CLOSED:
                        break;
                    case SEND_FEEDBACK:
                        break;
                    case OUTSIDE:
                        break;
                    case OPENED:
                        break;
                    case DISPLAY_ONCE:
                        break;
                    case PREVENT_MULTIPLE_FEEDBACK:
                        break;
                    default:
                        throw new IllegalStateException("Unexpected pisano action value: " + pisanoActions);
                }
            }
        })
        .build();

PisanoSDK.INSTANCE.init(pisanoSDKManager);
```

| Parameter Name | Require | Type  | Description  |
| ------- | --- | --- | --- |
| setApplicationId | Yes | String | The application ID that can be obtained from Pisano Dashboard  |
| setAccessKey | Yes | String | The access key can be obtained from Pisano Dashboard |
| setApiUrl | Yes | String | The URL of API that will be accessed |
| setFeedbackUrl | Yes | String | Base URL for survey |
| setEventUrl | No | String | Event Url for track |
| setCloseStatusCallback | No | ActionListener | Please check the table below for the details of this Pisano Actions Enum Class  |

### Show Method 
#### Kotlin

```yaml
import co.pisano.feedback.data.model.PisanoCustomer
import co.pisano.feedback.managers.PisanoSDK

PisanoSDK.show(
    flowId = "",
    language = "EN",
    pisanoCustomer = PisanoCustomer(
        name = null,
        email = null,
        phoneNumber = null,
        externalId = null,
        customAttributes = HashMap<String, Any>().apply {
           // your map items
            this["your_key"] = "your value"
        }
    )
)


```

#### Java
```yaml
import java.util.HashMap;
import co.pisano.feedback.data.model.PisanoCustomer;
import co.pisano.feedback.managers.PisanoSDK;

HashMap<String, String> payload = new HashMap<>();
payload.put("your_key", "your value");

HashMap<String, Object> customerAttributes = new HashMap<>();
customerAttributes.put("your_key", "your value");

PisanoCustomer pisanoCustomer = new PisanoCustomer("name", 
        "external_id",
        "email",
        "phoneNumber",
        customerAttributes);

PisanoSDK.INSTANCE.show(null, 
        "en",
        payload,
        pisanoCustomer);
```

### Clear Method | version: 1.2.17
#### Kotlin

```yaml
import co.pisano.feedback.managers.PisanoSDK

PisanoSDK.clearAction() 

```

#### Java

```yaml
import co.pisano.feedback.managers.PisanoSDK;

PisanoSDK.INSTANCE.clearAction();

```

| Parameter  Name | Type  | Description  |
| ------- | --- | --- |
| flowId | String | The ID of related flow. Can be obtained from Pisano Dashboard. Can be sent as empty string "" for default flow |
| language | String | Language code |
| payload | HashMap<String, String>  | Question and related answer in an array (mostly uses for pre-loaded responses to take transactional data(s))  |
| pisanoCustomer | PisanoCustomer | Please check the table below for the details of this PisanoCustomer |

## Pisano Customer Class

| Property  Name | Type  | Description  |
| ------- | --- | --- |
| email | String | The email of the customer  |
| phoneNumber | String | The phone number of the customer  |
| name | String | The name of the customer  |
| externalId | String | The external ID of the customer |
| customerAttributes | HashMap<String, String>  | Your custom keys and values  |

## Pisano Actions Enum Class 

| Event  Name | Description  |
| ------- | --- | 
| CLOSED | Closed Survey  |
| OPENED | Opened Survey | 
| SEND_FEEDBACK  | Send Feedback   |
| DISPLAY_ONCE  | Survey won't be shown due to the customer saw it before.  |
| PREVENT_MULTIPLE_FEEDBACK  | Survey won't be shown due to customer already submitted a feedback in a given time period.  |
| CHANNEL_QUOTA_EXCEEDED  | Survey won't be shown due to quota exceeded.  version: 1.2.17 |
| OUTSIDE | Others |



