package com.feytuo.laoxianghao;

import com.umeng.analytics.MobclickAgent;

import android.app.Activity;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.TextView;

public class MeAboutUsPrivacyPolicyActivity extends Activity {
	private TextView activityAboutUsPrivacyPolicy;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		setContentView(R.layout.activity_about_us_privacy_policy);
		super.onCreate(savedInstanceState);
		activityAboutUsPrivacyPolicy = (TextView) findViewById(R.id.privacy_policy_id);
		String html = "<p>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; 飞拓信息科技（以下简称&ldquo;feytuo&rdquo;）在此特别提醒您（下称&ldquo;用户&rdquo;）认真阅读、充分理解本《服务协议》（下称《协议》）:</p><p>&nbsp;&nbsp;&nbsp; 本《协议》是您与feytuo之间关于注册、登录、使用&ldquo;乡乡&rdquo;服务所订立的协议。本《协议》描述feytuo与用户之间关于乡乡服务相关方面的权利义务。&ldquo;用户&rdquo;是指使用、浏览本服务的个人或组织。</p><p>&nbsp;&nbsp;&nbsp; 用户应认真阅读、充分理解本《协议》中各条款，包括免除或者限制本公司责任的的免责条款及对用户权利的限制条款。请您审慎阅读并选择接受或不接受本《协议》（未成年人应在法定监护人陪同下阅读）。除非您接受本《协议》所有条款，否则您无权使用本《协议》所涉相关服务。您的使用等行为将视为对本《协议》的接受，并同意接受本《协议》各项条款的约束。 本《协议》可由feytuo随时更新，更新后的协议条款一旦公布即代替原来的协议条款，恕不再另行通知，用户可在网站随时查阅最新版协议条款。在修改《协议》条款后，如果用户不接受修改后的条款，请立即停止使用feytuo提供的服务，用户继续使用feytuo提供的服务将被视为已接受了修改后的协议。</p><p>&nbsp;一、服务规则</p><p>&nbsp;1、用户充分了解并同意，乡乡是一个基于用户手机为用户提供feytuo考试相关服务的产品，用户须对在乡乡上的提供信息的真实性、合法性、有效性承担全部责任，用户不得冒充他人名义；不得恶意使用帐号导致其他用户误认；否则feytuo有权立即停止提供服务，收回乡乡帐号并由用户独自承担由此而产生的一切法律责任。</p><p>&nbsp;2、用户须对在乡乡上所传送信息的真实性、合法性、有效性等全权负责。</p><p>&nbsp;3、feytuo保留因业务发展需要，单方面对本服务的全部或部分服务内容在任何时候不经任何通知的情况下进行变更、暂停、限制、终止或撤销乡乡服务的权利，用户需承担此风险。</p><p>&nbsp;4、乡乡提供的服务中可能包括广告，用户同意在使用过程中显示乡乡和第三方供应商、合作伙伴提供的广告并放弃向feytuo索取任何广告费用或收益。</p><p>&nbsp;5、用户不得利用乡乡或乡乡服务制作、上载、复制、发送如下内容：</p><p>&nbsp;（1）反对中华人民共和国宪法所确定的基本原则的；</p><p>&nbsp;（2）危害国家安全，泄露国家秘密，颠覆国家政权，破坏国家统一的；</p><p>&nbsp;（3）损害国家荣誉和利益的；</p><p>&nbsp;（4）煽动民族仇恨、民族歧视，破坏民族团结的；</p><p>&nbsp;（5）破坏国家宗教政策，宣扬邪教和封建迷信的；</p><p>&nbsp;（6）散布谣言，扰乱社会秩序，破坏社会稳定的；</p><p>&nbsp;（7）散布淫秽、色情、赌博、暴力、凶杀、恐怖或者教唆犯罪的；</p><p>&nbsp;（8）侮辱或者诽谤他人，侵害他人合法权益的；</p><p>&nbsp;（9）含有法律、行政法规禁止的其他内容的信息。</p><p>&nbsp;&nbsp;&nbsp; 由此衍生之任何损失或损害，feytuo有权删除任何前述内容并暂停您使用本服务的全部或部分。</p><p>&nbsp;二、用户权利与义务</p><p>&nbsp;1、乡乡帐号的所有权归feytuo所有，用户完成申请手续后，获得乡乡帐号的使用权，该使用权仅属于初始申请人，禁止赠与、借用、租用、转让或售卖。feytuo因经营需要，有权回收用户的乡乡帐号。</p><p>&nbsp;2、乡乡注册用户可享受免费获取考试相关的资讯及动态信息，查询相关考试信息等。</p><p>&nbsp;3、用户有责任妥善保管帐号信息及帐号的安全。用户同意在任何情况下不使用其他成员的帐号。在用户怀疑他人在使用您的帐号或密码时，您同意立即通知feytuo。</p><p>&nbsp;三、隐私保护政策</p><p>&nbsp;1、用户同意，个人隐私信息是指那些能够对用户进行个人辨识或涉及个人通信的信息，包括下列信息：用户真实姓名、真实证件号、手机号码、准考证信息、报名信息等。而非个人隐私信息是指用户对本服务的操作状态以及使用习惯等一些明确且客观反映在feytuo服务器端的基本记录信息和其他一切个人隐私信息范围外的普通信息，以及用户同意公开的上述隐私信息。</p><p>&nbsp;2、未经用户许可，feytuo不会向任何第三方公开或共享用户的个人隐私信息。保护用户隐私是我们的一项基本政策。</p><p>&nbsp;四、本软件商标</p><p>&nbsp;&nbsp;&nbsp; 本软件服务中所涉及的图形、文字或其组成，以及其他feytuo标志及产品、服务名称，均为feytuo之商标。未经feytuo事先书面同意，用户不得将feytuo标识以任何方式展示或使用或作其他处理，也不得向他人表面您有权展示、使用、或其他有权处理feytuo标识的行为。</p><p>&nbsp;五、法律责任及免责</p><p>&nbsp;1、用户违反本《协议》或相关的服务条款的规定，导致或产生的任何第三方主张的任何索赔、要求或损失，包括合理的律师费，用户同意赔偿feytuo与合作公司、关联公司，并使之免受损害。</p><p>&nbsp;2、用户因第三方如电信部门的通讯线路故障、技术问题、网络、手机故障、系统不稳定性及其他各种不可抗力原因而遭受的一切损失，feytuo及合作单位不承担责任。</p><p>&nbsp;3、因技术故障等不可抗事件影响到服务的正常运行的，feytuo及合作单位承诺在第一时间内与相关单位配合，及时处理进行修复，但用户因此而遭受的一切损失，feytuo及合作单位不承担责任。</p><p>&nbsp;4、用户须明白，使用本服务因涉及Internet服务，可能会受到各个环节不稳定因素的影响。因此，本服务存在因不可抗力、计算机病毒或黑客攻击原因等造成的服务中断及安全问题的侵扰，用户应加强信息安全意识并承担以上风险，对因此导致用户不能接受阅读信息等，feytuo不承担任何责任。</p><p>&nbsp;5、feytuo定义的信息内容包括：文字、软件、声音、相片、录像、图表；在广告中全部内容；feytuo为用户提供的商业信息，所有这些内容受版权、商标权和其他知识产权和所有权法律的保护。用户只能在feytuo和广告商授权下才能使用这些内容，不能擅自复制、修改、编撰这些内容或创造与内容有关的衍生品。</p><p>&nbsp;六、其他条款</p><p>&nbsp;1、本《协议》的订立、执行和解释及争议的解决均应适用中国法律。</p><p>&nbsp;2、用户同意如用户与feytuo就本协议内容或其执行发生任何争议，双方应尽量友好协商解决；协商不成时，任何一方均可向feytuo所在地的人民法院提起诉讼。</p><p>&nbsp;3、本《协议》所定的任何条款的部分或全部无效者，不影响其他条款的效力。</p><p>&nbsp;4、本《协议》的版权由feytuo所有，feytuo保留一切解释和修改权利。</p>";
		//html=""后面很长
		activityAboutUsPrivacyPolicy.setText(
            Html.fromHtml(html));

	}

	

	public void me_about_us_privacy_policy_ret(View v) {
		finish();
	}
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		MobclickAgent.onPageStart("MeAboutUsPrivacyPolicyActivity"); // 友盟统计页面
		MobclickAgent.onResume(this);
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		MobclickAgent.onPageEnd("MeAboutUsPrivacyPolicyActivity");// 友盟保证 onPageEnd 在onPause
													// 之前调用,因为 onPause 中会保存信息
		MobclickAgent.onPause(this);
	}

}
