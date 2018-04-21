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
					<img src={this.state.image} style={{maxWidth: '100px', maxHeight: '100px'}}/>
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