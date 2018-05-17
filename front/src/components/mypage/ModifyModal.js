import React from 'react';

class ModifyModal extends React.Component {
    constructor(props) {
        super(props);
    }

    componentDidMount() {
        this.modal = $('#modifyMyInfoModal');

        var self = this;
		var uploader = new ss.SimpleUpload({
            button: 'change_profile_image',
            url: '/api/user/profile-image?direct=y',
            name: 'profileImage',
            maxSize: 1024,
            onSubmit: function(filename, extension) {
                $('#profile_image_progress_bar').show();
                $('#change_profile_image').hide();
            },
            onComplete: function(filename, response) {
                if (response) {
                    self.props.mypage.myState.setMyInfo(null, null, response);
                    self.refs.image.src = response;
                }
                $('#profile_image_progress_bar').hide();
                $('#change_profile_image').show();
            }
        });
    }

    show(myInfo) {
        this.refs.emailNoti.textContent = '';
        this.refs.nicknameNoti.textContent = '';
        this.refs.image.src = myInfo.image;
        this.refs.email.value = myInfo.email;
        this.refs.nickname.value = myInfo.nickname;
        this.modal.modal();
    }

    save() {
        this.refs.emailNoti.textContent = '';
        this.refs.nicknameNoti.textContent = '';

        var data = {};
        data.email = this.refs.email.value;
        data.nickname = this.refs.nickname.value;

        var self = this;
		$.ajax({
			type: 'POST',
			url: '/api/user/update',
			data: JSON.stringify(data),
			async: false
		}).done(function() {
            self.props.mypage.myState.setMyInfo(data.email, data.nickname);
			self.modal.modal('hide');
		}).fail(function(res) {
			if (res.status == 409) {
                if (res.statusText === 'email') {
                    self.refs.emailNoti.textContent = '이메일이 중복되었습니다.';
                } else if (res.statusText === 'nickname') {
                    self.refs.nicknameNoti.textContent = '닉네임이 중복되었습니다.';
                }
            } else if (res.status == 400) {
                alert('잘못된 요청입니다.');
            } else if (res.status == 404) {
                alert('다시 시도해주세요.');
            } else {
                alert('server error.');
            }
		});
    }

    render() {
        return (
            <div className="modal fade" id="modifyMyInfoModal" tabIndex="-1" role="dialog" aria-hidden="true">
                <div className="modal-dialog">
                    <div className="modal-content">
                        <div className="modal-body">
                            <div className="row">
                                <div className="col-sm-12 col-md-12">
                                    <div style={{maxWidth: '300px', paddingTop: '20px'}}>
                                        <img ref='image' style={{width: '100px', height: '100px'}}/>
                                        <div style={{paddingTop: '5px', paddingBottom: '10px'}}>
                                            <a id='change_profile_image'>change image</a>
                                            <div style={{color: '#898989', fontSize: '13px'}}>max 512KB</div>
                                            <img id='profile_image_progress_bar' src='/css/processing.gif' hidden/>
                                        </div>
                                        <div className="form-group">
                                            <input ref='email' type="text" className="form-control" placeholder="email"/>
                                            <div ref="emailNoti" style={{fontSize: '13px', color: 'red', paddingTop: '5px'}}></div>
                                        </div>
                                        <div className="form-group">
                                            <input ref='nickname' type="text" className="form-control" placeholder="nickname"/>
                                            <div ref="nicknameNoti" style={{fontSize: '13px', color: 'red', paddingTop: '5px'}}></div>
                                        </div>
                                    </div>
                                </div>
                            </div>
                        </div>
                        <div className="modal-footer">
                            <button type="button" className="btn btn-default" data-dismiss="modal">닫기</button>
                            <button type="button" className="btn btn-primary" onClick={() => this.save()}>저장</button>
                        </div>
                    </div>
                </div>
            </div>
        );
    }
}

export default ModifyModal;