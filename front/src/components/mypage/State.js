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
		this.setState({
			email: 'kwonsm@icloud.com',
			nickname: 'KNero',
			image: 'https://scontent.xx.fbcdn.net/v/t1.0-1/p100x100/15094935_1225609307512845_7310823645782503183_n.jpg?oh=697e14377cecfe09c81a08c85cd7576e&oe=5AD98CB3',
			reviewCount: 5,
			impressionCount: 11
		});
	}

	render() {
		return (
			<div className='row' style={{paddingTop: '30px', paddingBottom: '30px'}}>
				<div className='col-sm-6 col-md-6' align='right' style={{paddingTop: '30px', paddingRight: '50px'}}>
					<img src={this.state.image}/>
				</div>
				<div className='col-sm-6 col-md-6' style={{paddingLeft: '30px'}}>
					<table width='100%'>
						<tbody>
							<tr>
								<td><h2>{this.state.email}</h2></td>
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