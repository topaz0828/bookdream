import React from 'react';

class SignUpModal extends React.Component {
    constructor(props) {
		super(props);
        this.state = {picture: ''};
    }
    
    componentDidMount() {
        this.modal = $('#signUpModal');
        var self = this;
        var uploader = new ss.SimpleUpload({
            button: 'file_uploader',
            url: '/api/user/profile-image',
            name: 'profileImage',
            maxSize: 512,
            onSubmit: function(filename, extension) {
                $('#progress_bar').show();
                $('#file_uploader').hide();
            },
            onComplete: function(filename, response) {
                if (response) {
                    self.refs.profilePicture.src = response;
                }
                $('#progress_bar').hide();
                $('#file_uploader').show();
            }
        });
    }

    show(data) {
        for (var i in data) {
            var info = data[i].split('=');
            if (info[0] === 'e') { //email
                this.refs.email.value = info[1];
            } else if (info[0] === 'n') { // name
                this.refs.nickname.value = decodeURIComponent(info[1]);
            } else if (info[0] === 'p') { //picture
                this.setState({picture: info[1]});
            }
        }
        this.modal.modal({backdrop: 'static'});
    }

    join() {
        var email = this.refs.email.value;
        var nickname = this.refs.nickname.value;
        this.refs.emailNoti.textContent = '';
        this.refs.nicknameNoti.textContent = '';

        if (email.length == 0 || email.indexOf('@') < 0 || email.indexOf('.') < 0) {
            this.refs.emailNoti.textContent = '이메일을 확인해 주세요.';
            return;
        }

        if (nickname.length == 0) {
            this.refs.nicknameNoti.textContent = '닉네임을 입력해 주세요.';
            return;
        }

        var self = this;
        $.ajax({
            type: 'POST',
            url: '/api/user/signUp',
            data: {nickname: nickname, email: email}
        }).done(function(res) {
            document.location = '/';
        }).fail(function(res) {
            if (res.status == 409) {
                if (res.responseText === 'email') {
                    self.refs.emailNoti.textContent = '이메일이 중복되었습니다.';
                } else if (res.responseText === 'nickname') {
                    self.refs.nicknameNoti.textContent = '닉네임이 중복되었습니다.';
                }
            } else if (res.status == 400) {
                alert('잘못된 요청입니다.');
                document.location = '/';
            } else if (res.status == 404) {
                alert('다시 시도해주세요.');
                document.location = '/';
            } else {
                alert('server error.');
                document.location = '/';
            }
        });
    }

    render() {
        return (
            <div className="modal fade" id="signUpModal" tabIndex="-1" role="dialog" aria-hidden="true">
                <div className="modal-dialog">
                    <div className="modal-content">
                        <div className="modal-header" style={{textAlign: 'center'}}>
                            <h1>Welcome!<br/>Marker</h1>
                        </div>
                        <div className="modal-body" style={{textAlign: 'center'}}>
                            <div align="center">
                                <img ref="profilePicture" src={this.state.picture} style={{width: '100px', height: '100px'}}/>
                            </div>
                            <div align="center">
                                <a id="file_uploader">change image</a>
                                <div style={{color: '#898989', fontSize: '13px', paddingTop: '5px'}}>max 512KB</div>
                                <img id="progress_bar" src="/css/processing.gif" hidden/>
                            </div>
                            <div style={{paddingTop: '10px'}}>
                                <div className="input-group">
                                    <span className="input-group-addon">이메일</span>
                                    <input ref='email' type='text' className="form-control"/>
                                </div>
                                <div ref="emailNoti" style={{fontSize: '13px', color: 'red', paddingTop: '5px'}}></div>
                            </div>
                            <div>
                                <div className="input-group">
                                    <span className="input-group-addon">닉네임</span>
                                    <input ref='nickname' type='text' className="form-control"/>
                                </div>
                                <div ref="nicknameNoti" style={{fontSize: '13px', color: 'red', paddingTop: '5px'}}></div>
                            </div>
                        </div>
                        <div className="modal-footer">
                            <button type="button" className="btn btn-default" data-dismiss="modal">취소</button>
                            <button type="button" className="btn btn-info" onClick={() => {this.join()}}>가입</button>
                        </div>
                    </div>
                </div>
            </div>
        );
    }
}

export default SignUpModal;