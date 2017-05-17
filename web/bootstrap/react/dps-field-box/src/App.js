import React, { Component } from 'react';
import DynamicPropertySet from './DynamicPropertySet';
import logo from './logo.svg';
import './App.css';

class App extends Component {

  constructor(props) {
    super(props);
    var testJson = require('./testJson.json');

    this.state = {
      fields: testJson
    };

    this.handleFieldChange = this.handleFieldChange.bind(this);
    this.handleSubmit = this.handleSubmit.bind(this);
    this.handleJsonChange = this.handleJsonChange.bind(this);
  }

  handleSubmit(event) {
    alert('A name was submitted: ' + this.state.value);
    event.preventDefault();
  }

  handleJsonChange(event){
    this.setState({fields: JSON.parse(event.target.value)});
  }

  handleFieldChange(name, value) {
    const field = this.state.fields.find(field => field.name === name);
    field.value = value;
    //alert(value);

//    implicit state change => forceUpdate
//    this.forceUpdate(() => {
//      //this.setState({ allFieldsFilled: this._allFieldsFilled() });
//
//      if (field.reloadOnChange || field.autoRefresh) {
//        this._reloadOnChange(name);
//      }
//    });
  }

  render() {
    return (
      <div className="App">
        <div className="App-header">
          <img src={logo} className="App-logo" alt="logo" />
          <h2>React component for DynamicPropertySet</h2>
        </div>
        <div className="App-intro">
          <div className="container">
            <div className="row">
              <div className="col-md-7">
                <form onSubmit={this.handleSubmit} className="bs-example">
                  <DynamicPropertySet fields={this.state.fields} onChange={this.handleFieldChange}/>
                  <div className="text-center">
                    <button type="submit" className="btn btn-primary btn-primary-spacing">Submit</button>
                    <button type="button" className="btn btn-default btn-primary-spacing">Cancel</button>
                  </div>
                </form>
              </div>
              <div className="col-md-5">
                <textarea rows="20" name="inputJson" className="form-control" defaultValue={JSON.stringify(this.state.fields, null, 4)}
                          onChange={this.handleJsonChange} />
              </div>
            </div>
          </div>
        </div>
      </div>
    );
  }

}

export default App;
