import React from 'react';

class State extends React.Component {
	constructor(props) {
		super(props);
		this.state = {
			email: '',
			nickname: '',
			image: '',
			reviewCount: 0,
			impressionCount: 0
		};
	}

	componentDidMount() {
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
        		$('#profile_image').attr('src', response);
            }
            $('#profile_image_progress_bar').hide();
            $('#change_profile_image').show();
        }
    });
	}

	getState() {
		var self = this;
		$.get("/api/user/profile", function(data) {
			var info = JSON.parse(data);
			self.setState({
				email: info.email,
				nickname: info.nickname,
				image: info.image,
				reviewCount: info.reviewCount,
				impressionCount: info.impressionCount
			});

		});
	}

	render() {
		return (
			<div className='row' style={{paddingTop: '30px', paddingBottom: '30px'}}>
				<div className='col-sm-6 col-md-6' align='right' style={{paddingTop: '30px', paddingRight: '50px'}}>
					<img id='profile_image' src={this.state.image} style={{width: '100px', height: '100px'}}/>
					<div align='right' style={{paddingTop: '5px'}}>
						<a id='change_profile_image'>change image</a>
						<img id='profile_image_progress_bar' src='/css/processing.gif' hidden/>
					</div>
				</div>
				<div className='col-sm-6 col-md-6' style={{paddingLeft: '30px'}}>
					<table width='100%'>
						<tbody>
							<tr>
								<td><h3>{this.state.email}</h3></td>
							</tr>
							<tr>
								<td>
									<blockquote>
										<p>{this.state.nickname}</p>
										<p>
											Review <strong>{this.state.reviewCount}</strong>&nbsp;&nbsp;
											Impression <strong>{this.state.impressionCount}</strong>
										</p>
									</blockquote>
								</td>
							</tr>
						</tbody>
					</table>			
				</div>
			</div>
		);
	}
}

export default State;