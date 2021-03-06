import React from 'react';

class Card extends React.Component {
	constructor(props) {
		super(props);
		this.data = this.props.data;
		this.showDetailModal = () => {
			this.props.parent.app.showDetailModal(this.data.contentsId);
		}

		this.onMouseOver = () => {
			this.thumbnameDiv.style.cursor = 'pointer';
		}
		this.onMouseOut = () => {
			this.thumbnameDiv.style.cursor = 'arrow';
		}
	}

	render() {
		return (
			<div ref={ref => this.thumbnameDiv = ref} className={'thumbnail bookdream-card'} onClick={this.showDetailModal} onMouseOver={this.onMouseOver} onMouseOut={this.onMouseOut}>
				<div className='caption'>
					<div style={{paddingBottom:'10px'}}>
						<table width='100%'>
							<tbody>
							<tr>
								<td valign='top'>
									<h5 name='title' style={{paddingRight:'20px'}}><strong>{this.data.title}</strong></h5>
									<p><span name='author'>{this.data.author}</span></p>
									<p><span name='publisher'>{this.data.publisher}</span></p>
								</td>
								<td style={{paddingRight:'10px'}}>
									<img name='image' src={this.data.image} width='100px' style={{float:'right'}}/>
								</td>
							</tr>
							</tbody>
						</table>
					</div>
					<div align='left'><p><span name='text'>{this.data.text}</span></p></div>
					<div align='right'>
						<img name='profile_image' src={this.data.profileImage} className='profile_image'/>&nbsp;
						<span name='nickname'>{this.data.nickname}</span>
					</div>

					<input type='hidden' name='contentsId' value={this.data.contentsId}/>
					<input type='hidden' name='bookId' value={this.data.bookId}/>
					<input type='hidden' name='type' value={this.data.type}/>
					<input type='hidden' name='my' value={this.data.my}/>
				</div>
			</div>
		);
	}
}

export default Card;